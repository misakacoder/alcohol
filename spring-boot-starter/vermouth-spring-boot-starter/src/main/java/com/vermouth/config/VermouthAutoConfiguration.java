package com.vermouth.config;

import com.alipay.sofa.jraft.rpc.RpcProcessor;
import com.vermouth.core.RaftServer;
import com.vermouth.listener.RaftServerStartupListener;
import com.vermouth.processor.RequestProcessor;
import com.vermouth.properties.RaftProperties;
import com.vermouth.properties.VermouthProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import java.nio.file.Paths;
import java.util.List;

@ComponentScan("com.vermouth")
@ConditionalOnProperty(name = "vermouth.enabled", havingValue = "true")
public class VermouthAutoConfiguration {

    @Bean(destroyMethod = "shutdown")
    public RaftServer raftServer(Environment environment,
                                 VermouthProperties properties,
                                 List<RaftServerStartupListener> listeners,
                                 List<RpcProcessor<?>> rpcProcessors,
                                 List<RequestProcessor> requestProcessors) {
        RaftProperties raftProperties = new RaftProperties();
        String dataHome = properties.getHome();
        if (StringUtils.isBlank(dataHome)) {
            dataHome = Paths.get(System.getProperty("user.home"), "vermouth").toFile().getAbsolutePath();
        }
        raftProperties.setDataDir(dataHome);
        int port = properties.getPort();
        if (port <= 0) {
            port = environment.getProperty("server.port", int.class, raftProperties.getPort()) - 10;
        }
        raftProperties.setPort(port);
        List<String> nodes = properties.getNodes();
        if (nodes != null) {
            raftProperties.setConf(String.join(",", nodes));
        }
        RaftServer raftServer = new RaftServer(raftProperties);
        raftServer.addRpcProcessor(rpcProcessors);
        raftServer.addRequestProcessor(requestProcessors);
        listeners.forEach(p -> p.before(raftServer));
        raftServer.startup();
        listeners.forEach(p -> p.after(raftServer));
        return raftServer;
    }
}