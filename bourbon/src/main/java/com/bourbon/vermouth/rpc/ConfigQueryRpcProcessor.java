package com.bourbon.vermouth.rpc;

import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import com.bourbon.config.ConfigLoader;
import com.bourbon.vermouth.entity.ConfigQuery;
import com.bourbon.vermouth.entity.ConfigQueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConfigQueryRpcProcessor implements RpcProcessor<ConfigQuery> {

    @Autowired
    private ConfigLoader configLoader;

    @Override
    public void handleRequest(RpcContext rpcContext, ConfigQuery configQuery) {
        Map<String, Object> configs = configLoader.load(configQuery.getFilename(), configQuery.getIsAppName(), configQuery.getProfileList());
        ConfigQueryResponse.Builder builder = ConfigQueryResponse.newBuilder();
        configs.forEach((k, v) -> builder.putConfigs(k, v.toString()));
        rpcContext.sendResponse(builder.build());
    }

    @Override
    public String interest() {
        return ConfigQuery.class.getName();
    }
}
