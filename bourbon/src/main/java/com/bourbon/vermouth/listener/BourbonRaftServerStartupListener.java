package com.bourbon.vermouth.listener;

import com.bourbon.vermouth.entity.ConfigQuery;
import com.bourbon.vermouth.entity.ConfigQueryResponse;
import com.vermouth.core.RaftServer;
import com.vermouth.listener.RaftServerStartupListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BourbonRaftServerStartupListener implements RaftServerStartupListener {

    @Override
    public void before(RaftServer raftServer) {
        raftServer.addProtobufSerializer(List.of(ConfigQuery.getDefaultInstance()));
        raftServer.addRpcResponse(Map.of(ConfigQuery.getDefaultInstance(), ConfigQueryResponse.getDefaultInstance()));
    }
}
