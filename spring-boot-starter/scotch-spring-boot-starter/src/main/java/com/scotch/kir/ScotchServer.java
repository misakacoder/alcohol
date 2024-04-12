package com.scotch.kir;

import com.kir.annotation.PostMapping;
import com.kir.annotation.RequestBody;
import com.kir.annotation.RequestMapping;
import com.scotch.data.Producer;

import java.util.Map;
import java.util.Set;

@RequestMapping("/scotch")
public interface ScotchServer {

    @PostMapping(value = "/register")
    void register(@RequestBody Producer producer);

    @PostMapping("/pull")
    Map<String, Set<Producer>> pull(@RequestBody Set<String> appNames);

    @PostMapping("/heartbeat")
    void heartbeat(@RequestBody Producer producer);
}
