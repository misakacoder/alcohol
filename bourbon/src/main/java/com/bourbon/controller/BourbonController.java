package com.bourbon.controller;

import com.bourbon.service.BourbonService;
import com.bourbon.vermouth.ConfigPublisherRequestProcessor;
import com.bourbon.vermouth.entity.ConfigPublisher;
import com.google.protobuf.ByteString;
import com.vermouth.core.RaftServer;
import com.vermouth.entity.Operation;
import com.vermouth.entity.Request;
import com.vermouth.serializer.SerializerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/bourbon")
public class BourbonController {

    @Autowired(required = false)
    private RaftServer raftServer;

    @Autowired
    private BourbonService bourbonService;

    @GetMapping("/config/{filename}")
    public Map<String, Object> config(@PathVariable String filename, Boolean isAppName, String profile) {
        return bourbonService.config(filename, isAppName, profile);
    }

    @GetMapping("/listen")
    public void listen(HttpServletRequest request, HttpServletResponse response) {
        bourbonService.listen(request, response);
    }

    @GetMapping("/publish/config/{appName}")
    public void publish(@PathVariable String appName, Boolean isAppName, String profile) {
        ConfigPublisher publisher = new ConfigPublisher();
        publisher.setFilename(appName);
        publisher.setAppName(isAppName);
        publisher.setProfile(profile);
        Request request = Request.newBuilder()
                .setGroup(ConfigPublisherRequestProcessor.GROUP)
                .setData(ByteString.copyFrom(SerializerFactory.get().serialize(publisher)))
                .setOperation(Operation.WRITE)
                .build();
        if (raftServer != null) {
            raftServer.commit(request);
        } else {
            bourbonService.publish(appName, isAppName, profile);
        }
    }
}
