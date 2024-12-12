package com.gin.controller;

import cn.hutool.json.JSONUtil;
import com.bourbon.refresh.RefreshScope;
import com.gin.kir.Gin;
import com.gin.kir.Hero;
import com.gin.properties.ServerProperties;
import com.misaka.annotation.BasicAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RefreshScope
@RestController
public class GinController {

    private static final Logger log = LoggerFactory.getLogger(GinController.class);

    @Autowired
    private Gin gin;

    @Autowired
    private Hero hero;

    @Autowired
    private Environment environment;

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

    @BasicAuth
    @GetMapping("/gin")
    public Object gin(@RequestParam Integer limit) {
        log.info("{}", JSONUtil.toJsonStr(gin.get()));
        return gin.list(limit);
    }
}
