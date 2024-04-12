package com.scotch.controller;

import com.misaka.annotation.BasicAuth;
import com.scotch.core.ScotchRegistry;
import com.scotch.data.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

@BasicAuth
@RestController
@RequestMapping("/scotch")
public class ScotchController {

    @Autowired
    private ScotchRegistry scotchRegistry;

    @GetMapping("/list")
    public Map<String, Set<Producer>> list() {
        return scotchRegistry.list();
    }

    @PostMapping("/register")
    public void register(HttpServletRequest httpServletRequest, @RequestBody Producer producer) {
        producer.setHost(httpServletRequest.getRemoteAddr());
        scotchRegistry.register(producer);
    }

    @PostMapping("/pull")
    public Map<String, Set<Producer>> pull(@RequestBody Set<String> appNames) {
        return scotchRegistry.pull(appNames);
    }

    @PostMapping("/heartbeat")
    public void heartbeat(HttpServletRequest httpServletRequest, @RequestBody Producer producer) {
        producer.setHost(httpServletRequest.getRemoteAddr());
        scotchRegistry.heartbeat(producer);
    }
}
