package com.vermouth.rpc;

import com.alipay.remoting.NamedThreadFactory;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import com.vermouth.core.RaftServer;
import com.vermouth.entity.Operation;
import com.vermouth.entity.Request;
import com.vermouth.entity.Response;
import com.vermouth.entity.ResponseFactory;
import com.vermouth.processor.RequestProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RequestRpcProcessor implements RpcProcessor<Request> {

    private static final Logger log = LoggerFactory.getLogger(RequestRpcProcessor.class);

    private final Executor executor;

    private final RaftServer raftServer;

    public RequestRpcProcessor(RaftServer raftServer) {
        this.executor = Executors.newFixedThreadPool(8, new NamedThreadFactory("raft-grpc-executor"));
        this.raftServer = raftServer;
    }

    @Override
    public void handleRequest(RpcContext rpcContext, Request request) {
        String group = request.getGroup();
        Response response = null;
        try {
            Operation operation = request.getOperation();
            //此方法一定follower重定向到leader时才会调用的，所以执行此方法的一定是leader
            //既然是leader，那么当进行读操作时，不需要提交到状态机，直接调用对应状态机中的RequestProcessor进行处理
            if (operation == Operation.READ) {
                RequestProcessor processor = raftServer.getRaftGroup(group).getRequestProcessor();
                response = processor.process(request);
            } else {
                CompletableFuture<Response> future = raftServer.commit(request);
                response = future.get();
            }
        } catch (Exception e) {
            log.error("", e);
            response = ResponseFactory.buildError(e.getMessage());
        }
        rpcContext.sendResponse(response);
    }

    @Override
    public String interest() {
        return Request.class.getName();
    }

    @Override
    public Executor executor() {
        return executor;
    }
}
