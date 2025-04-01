package com.bourbon.refresh;

import com.bourbon.properties.BourbonProperties;
import com.bourbon.util.BourbonUtil;
import com.bourbon.util.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class RefreshEnvRunner implements CommandLineRunner, Runnable {

    private static final Logger log = LoggerFactory.getLogger(RefreshEnvRunner.class);

    @Value("${spring.application.name:}")
    private String appName;

    @Value("${spring.profiles.active:}")
    private String profile;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BourbonProperties bourbonProperties;

    @Autowired
    private ConfigurableEnvironment environment;

    @Override
    public void run(String... args) {
        new Thread(this).start();
    }

    @Override
    public void run() {
        if (bourbonProperties.isEnabled()) {
            String url = bourbonProperties.getUrl();
            String[] headers = BourbonUtil.getAuthorizationHeader(bourbonProperties.getBasic());
            if (StringUtils.hasText(url) && StringUtils.hasText(appName)) {
                while (true) {
                    try {
                        HttpResponse<String> response = HttpUtil.get(String.format("%s/bourbon/listen?appName=%s", url, appName), Duration.ofSeconds(80L), headers);
                        if (response.statusCode() == 200) {
                            Map<String, Object> applicationMap = objectMapper.readValue(response.body(), new TypeReference<>() {
                            });
                            environment.getPropertySources().replace(appName, new MapPropertySource(appName, applicationMap));
                            RefreshEnvScope.clear();
                            log.info("Load bourbon configuration success");
                        }
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            }
        }
    }
}
