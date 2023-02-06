package com.gin.controller;

import cn.hutool.json.JSONUtil;
import com.bourbon.refresh.RefreshScope;
import com.gin.kir.Hero;
import com.gin.properties.ServerProperties;
import com.misaka.annotation.BasicAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RefreshScope
@RestController
public class GinController {

    @Autowired
    private Hero hero;

    @Autowired
    private ServerProperties serverProperties;

    @Value("object:server")
    private ServerProperties serverPropertiesByValue;

    @BasicAuth
    @GetMapping("/server")
    public Map<String, Object> printServer() {
        return Map.of(
                "serverProperties", JSONUtil.parse(serverProperties),
                "serverPropertiesByValue", JSONUtil.parse(serverPropertiesByValue)
        );
    }

    @BasicAuth
    @GetMapping("/search")
    public Object search(@RequestParam String id) {
        return hero.search(id);
    }
}
