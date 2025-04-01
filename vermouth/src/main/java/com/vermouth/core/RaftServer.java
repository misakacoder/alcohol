package com.vermouth.core;

import com.alipay.remoting.NamedThreadFactory;
import com.alipay.remoting.util.StringUtils;
import com.alipay.sofa.jraft.*;
import com.alipay.sofa.jraft.closure.ReadIndexClosure;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.core.CliServiceImpl;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.entity.Task;
import com.alipay.sofa.jraft.error.RaftError;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.*;
import com.alipay.sofa.jraft.rpc.impl.AbstractClientService;
import com.alipay.sofa.jraft.rpc.impl.GrpcRaftRpcFactory;
import com.alipay.sofa.jraft.rpc.impl.MarshallerRegistry;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import com.alipay.sofa.jraft.util.Endpoint;
import com.alipay.sofa.jraft.util.RpcFactoryHelper;
import com.google.protobuf.Message;
import com.vermouth.entity.Request;
import com.vermouth.entity.Response;
import com.vermouth.processor.RequestProcessor;
import com.vermouth.properties.RaftProperties;
import com.vermouth.rpc.RequestRpcProcessor;
import com.vermouth.util.NetUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class RaftServer {

    private static final Logger log = LoggerFactory.getLogger(RaftServer.class);

    private final RaftProperties raftProperties;

    private final Map<String, RaftGroup> raftGroups;

    private final List<Message> protobufSerializers;

    private final Map<Message, Message> rpcResponses;

    private final List<RpcProcessor<?>> rpcProcessors;

    private final List<RequestProcessor> requestProcessors;

    private final ExecutorService raftExecutor;

    private final ExecutorService cliExecutor;

    private final ScheduledExecutorService scheduledExecutor;

    private volatile boolean isStarted = false;

    private volatile boolean isShutdown = false;

    private PeerId peerId;

    private RpcServer rpcServer;

    private CliService cliService;

    private CliClientService cliClientService;

    public RaftServer(RaftProperties raftProperties) {
        this.raftProperties = raftProperties;
        this.raftGroups = new HashMap<>();
        this.protobufSerializers = new ArrayList<>();
        this.rpcResponses = new HashMap<>();
        this.rpcProcessors = new ArrayList<>();
        this.requestProcessors = new ArrayList<>();
        this.raftExecutor = Executors.newFixedThreadPool(32, new NamedThreadFactory("raft-server-executor"));
        this.cliExecutor = Executors.newFixedThreadPool(32, new NamedThreadFactory("raft-client-executor"));
        this.scheduledExecutor = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("raft-client-refresh-executor"));
    }

    public synchronized void addProtobufSerializer(List<Message> protobufSerializers) {
        if (!isStarted) {
            this.protobufSerializers.addAll(protobufSerializers);
        }
    }

    public synchronized void addRpcResponse(Map<Message, Message> rpcResponses) {
        if (!isStarted) {
            this.rpcResponses.putAll(rpcResponses);
        }
    }

    public synchronized void addRpcProcessor(List<RpcProcessor<?>> rpcProcessors) {
        if (!isStarted) {
            this.rpcProcessors.addAll(rpcProcessors);
        }
    }

    public synchronized void addRequestProcessor(List<RequestProcessor> requestProcessors) {
        if (!isStarted) {
            this.requestProcessors.addAll(requestProcessors);
        }
    }

    public synchronized void startup() {
        if (!isStarted) {
            log.info("The raft server is starting.");
            try {
                initRpcFactory();
                initServer();
                initClientService(new CliOptions());
                createRaftGroup();
                isStarted = true;
                log.info("The raft server is started.");
            } catch (Exception e) {
                log.error("An error occurred during the Raft server startup.", e);
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void shutdown() {
        if (!isShutdown) {
            try {
                log.info("The raft server is shutting down.");
                for (Map.Entry<String, RaftGroup> entry : raftGroups.entrySet()) {
                    RaftGroup raftGroup = entry.getValue();
                    raftGroup.getNode().shutdown();
                    raftGroup.getRaftGroupService().shutdown();
                }
                cliService.shutdown();
                cliClientService.shutdown();
                raftExecutor.shutdown();
                cliExecutor.shutdown();
                scheduledExecutor.shutdown();
                log.info("The raft server has been shut down.");
            } catch (Exception e) {
                log.error("An error occurred during the raft server shutdown.", e);
            }
            isShutdown = true;
        }
    }

    public RaftGroup getRaftGroup(String group) {
        RaftGroup raftGroup = raftGroups.get(group);
        if (raftGroup == null) {
            throw new RuntimeException(String.format("Get raft group '%s' error, the group does not exist.", group));
        }
        return raftGroup;
    }

    public CompletableFuture<Response> get(Request request) {
        RaftGroup raftGroup = getRaftGroup(request.getGroup());
        Node node = raftGroup.getNode();
        RequestProcessor requestProcessor = raftGroup.getRequestProcessor();
        CompletableFuture<Response> future = RaftClosure.newTimeoutFuture();
        AtomicBoolean error = new AtomicBoolean(true);
        try {
            byte[] context = new byte[0];
            node.readIndex(context, new ReadIndexClosure() {
                @Override
                public void run(Status status, long index, byte[] context) {
                    if (status.isOk()) {
                        try {
                            Response response = requestProcessor.process(request);
                            future.complete(response);
                        } catch (Exception e) {
                            future.completeExceptionally(e);
                        }
                    } else {
                        log.error("An unknown error occurred during get request, the status is {}.", status);
                        error.set(true);
                    }
                }
            });
        } catch (Exception e) {
            log.error("An unknown error occurred during get request.", e);
            error.set(true);
        }
        if (error.get()) {
            RaftClosure closure = new RaftClosure(future);
            closure.setRequest(request);
            redirectToLeader(request, closure);
        }
        return future;
    }

    public CompletableFuture<Response> commit(Request request) {
        RaftGroup raftGroup = getRaftGroup(request.getGroup());
        RaftClosure closure = new RaftClosure();
        closure.setRequest(request);
        Node node = raftGroup.getNode();
        if (node.isLeader()) {
            applyByLeader(node, request, closure);
        } else {
            redirectToLeader(request, closure);
        }
        return closure.getFuture();
    }

    public CompletableFuture<Response> invokeAsync(Endpoint endpoint, Message message) {
        RaftClosure closure = new RaftClosure();
        AbstractClientService cliClientService = (AbstractClientService) this.cliClientService;
        try {
            cliClientService.getRpcClient().invokeAsync(endpoint, message, new Callback(closure), raftProperties.getRequestTimeoutMillis());
        } catch (Exception e) {
            closure.runError(e.getMessage());
        }
        return closure.getFuture();
    }

    private void applyByLeader(Node node, Request request, RaftClosure closure) {
        Task task = new Task();
        task.setData(ByteBuffer.wrap(request.toByteArray()));
        task.setDone(closure);
        node.apply(task);
    }

    private void redirectToLeader(Request request, RaftClosure closure) {
        PeerId peerId = RouteTable.getInstance().selectLeader(request.getGroup());
        if (peerId != null) {
            Endpoint endpoint = peerId.getEndpoint();
            AbstractClientService cliClientService = (AbstractClientService) this.cliClientService;
            try {
                cliClientService.getRpcClient().invokeAsync(endpoint, request, new Callback(closure), raftProperties.getRequestTimeoutMillis());
            } catch (Exception e) {
                closure.runError(e.getMessage());
            }
        } else {
            closure.runError("Leader not found");
        }
    }

    private void initRpcFactory() {
        GrpcRaftRpcFactory raftRpcFactory = (GrpcRaftRpcFactory) RpcFactoryHelper.rpcFactory();
        raftRpcFactory.registerProtobufSerializer(Request.class.getName(), Request.getDefaultInstance());
        raftRpcFactory.registerProtobufSerializer(Response.class.getName(), Response.getDefaultInstance());
        protobufSerializers.forEach(p -> raftRpcFactory.registerProtobufSerializer(p.getClass().getName(), p));

        MarshallerRegistry marshallerRegistry = raftRpcFactory.getMarshallerRegistry();
        marshallerRegistry.registerResponseInstance(Request.class.getName(), Response.getDefaultInstance());
        rpcResponses.forEach((k, v) -> marshallerRegistry.registerResponseInstance(k.getClass().getName(), v));
    }

    private void initServer() {
        int port = raftProperties.getPort();
        PeerId peerId = PeerId.parsePeer(String.format("%s:%s", ip(), port));
        this.peerId = peerId;
        RpcServer rpcServer = RpcFactoryHelper.rpcFactory().createRpcServer(peerId.getEndpoint());
        rpcServer.registerProcessor(new RequestRpcProcessor(this));
        rpcProcessors.forEach(rpcServer::registerProcessor);
        RaftRpcServerFactory.addRaftRequestProcessors(rpcServer, raftExecutor, cliExecutor);
        if (!rpcServer.init(null)) {
            throw new RuntimeException("RaftServer init error.");
        }
        this.rpcServer = rpcServer;
    }

    private void initClientService(CliOptions cliOptions) {
        cliService = new CliServiceImpl();
        cliClientService = new CliClientServiceImpl();
        if (!cliService.init(cliOptions) || !cliClientService.init(cliOptions)) {
            throw new RuntimeException("Raft ClientService init error.");
        }
    }

    private void createRaftGroup() throws Exception {
        for (RequestProcessor processor : requestProcessors) {
            String group = processor.group();
            File dataDir = Paths.get(raftProperties.getDataDir(), group).toFile();
            FileUtils.forceMkdir(dataDir);
            NodeOptions nodeOptions = new NodeOptions();
            //选举超时时间
            nodeOptions.setElectionTimeoutMs(raftProperties.getElectionTimeoutMillis());
            //快照时间间隔
            nodeOptions.setSnapshotIntervalSecs(raftProperties.getSnapshotIntervalSecond());
            //节点日志存储路径
            nodeOptions.setLogUri(new File(dataDir, "log").getAbsolutePath());
            //节点元数据存储路径
            nodeOptions.setRaftMetaUri(new File(dataDir, "meta").getAbsolutePath());
            //节点快照存储路径
            nodeOptions.setSnapshotUri(new File(dataDir, "snapshot").getAbsolutePath());

            Configuration configuration = new Configuration();
            String conf = raftProperties.getConf();
            if (StringUtils.isNotBlank(conf)) {
                if (!configuration.parse(conf)) {
                    throw new IllegalArgumentException(String.format("Parse cluster list from '%s' error.", conf));
                }
            } else {
                configuration.addPeer(peerId);
            }
            nodeOptions.setInitialConf(configuration);

            RaftStateMachine raftStateMachine = new RaftStateMachine(processor);
            nodeOptions.setFsm(raftStateMachine);

            RaftGroupService raftGroupService = new RaftGroupService(group, peerId, nodeOptions, rpcServer, true);
            Node node = raftGroupService.start(false);
            RouteTable.getInstance().updateConfiguration(group, configuration);

            scheduledExecutor.scheduleWithFixedDelay(() -> addPeer(group, peerId, configuration), 0, 1000, TimeUnit.MILLISECONDS);

            Random random = new Random();
            int electionTimeoutMs = nodeOptions.getElectionTimeoutMs();
            long period = electionTimeoutMs + random.nextInt(5 * 1000);
            scheduledExecutor.scheduleAtFixedRate(() -> refreshRouteTable(group), electionTimeoutMs, period, TimeUnit.MILLISECONDS);
            raftGroups.put(group, new RaftGroup(node, raftGroupService, raftStateMachine, processor));
        }
    }

    private String ip() {
        List<String> ipList = NetUtil.ip();
        if (ipList.isEmpty()) {
            return NetUtil.LOCAL_IP;
        } else {
            String ip = ipList.get(0);
            log.info("Multiple IP addresses found, using {}.", ip);
            return ip;
        }
    }

    private void addPeer(String groupId, PeerId peerId, Configuration configuration) {
        if (!isShutdown) {
            Endpoint endpoint = peerId.getEndpoint();
            try {
                List<PeerId> peers = cliService.getPeers(groupId, configuration);
                if (!peers.contains(peerId)) {
                    Status status = cliService.addPeer(groupId, configuration, peerId);
                    if (!status.isOk()) {
                        log.error("Peer {} join the cluster error, the status is {}.", endpoint, status);
                    }
                }
            } catch (Exception e) {
                log.error("Peer {} join the cluster error, the exception is {}.", endpoint, e.getMessage());
            }
        }
    }

    private void refreshRouteTable(String group) {
        try {
            RouteTable routeTable = RouteTable.getInstance();
            Status status = routeTable.refreshLeader(cliClientService, group, 5000);
            if (!status.isOk()) {
                log.error("Refresh leader for group '{}' error, the status is {}.", group, status);
            }
            status = routeTable.refreshConfiguration(cliClientService, group, 5000);
            if (!status.isOk()) {
                log.error("Refresh route configuration for group '{}' error, the status is : {}.", group, status);
            }
        } catch (Exception e) {
            log.error("Refresh raft metadata for group '{}' error, the exception is {}.", group, e.getMessage());
        }
    }

    private class Callback implements InvokeCallback {

        private final RaftClosure closure;

        public Callback(RaftClosure closure) {
            this.closure = closure;
        }

        @Override
        public void complete(Object object, Throwable throwable) {
            if (throwable != null) {
                closure.setThrowable(throwable);
                closure.run(new Status(RaftError.UNKNOWN, throwable.getMessage()));
            } else if (object instanceof Response) {
                Response response = (Response) object;
                closure.setResponse(response);
                closure.run(Status.OK());
            } else {
                closure.runError("unknown error");
            }
        }

        @Override
        public Executor executor() {
            return cliExecutor;
        }
    }
}
