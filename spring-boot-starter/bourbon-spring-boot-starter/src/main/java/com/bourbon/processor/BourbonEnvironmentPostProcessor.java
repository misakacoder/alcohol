package com.bourbon.processor;

import com.bourbon.env.OriginTrackedYamlLoader;
import com.bourbon.properties.BourbonProperties;
import com.bourbon.util.BourbonUtil;
import com.bourbon.util.HttpUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@Order(Integer.MIN_VALUE)
public class BourbonEnvironmentPostProcessor implements EnvironmentPostProcessor {

    public static final String BOURBON_PROPERTY_SOURCE_NAME = "applicationProperties";
    private static final String CONFIG_NAME = "bootstrap.yml";
    private static final String APPLICATION_NAME_KEY = "spring.application.name";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ClassPathResource bootstrap = new ClassPathResource(CONFIG_NAME);
        if (!bootstrap.exists()) {
            throw new RuntimeException(String.format("Application run failed, config %s not found!", CONFIG_NAME));
        }
        OriginTrackedYamlLoader yamlLoader = new OriginTrackedYamlLoader();
        yamlLoader.setResources(bootstrap);
        Map<String, Object> bootstrapMap = yamlLoader.loadAsMap();
        environment.getPropertySources().addLast(new MapPropertySource("bootstrapProperties", bootstrapMap));
        BourbonProperties bourbonProperties = Binder.get(environment).bind("bourbon", Bindable.of(BourbonProperties.class)).orElseGet(BourbonProperties::new);
        if (bourbonProperties.isEnable()) {
            String url = bourbonProperties.getUrl();
            String appName = environment.getProperty(APPLICATION_NAME_KEY);
            String profile = environment.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
            url = BourbonUtil.urlJoiner(url, appName, profile);
            if (StringUtils.hasText(url)) {
                try {
                    String[] headers = BourbonUtil.getAuthorizationHeader(bourbonProperties.getBasic());
                    HttpResponse<String> response = HttpUtil.get(url, Duration.ofSeconds(5L), headers);
                    if (response.statusCode() != 200) {
                        String message = String.format("Failed to load bourbon configuration, the response code is %s and the response body is %s", response.statusCode(), response.body());
                        throw new RuntimeException(message);
                    }
                    Map<String, Object> applicationMap = new ObjectMapper().readValue(response.body(), new TypeReference<>() {
                    });
                    environment.getPropertySources().addLast(new MapPropertySource(BOURBON_PROPERTY_SOURCE_NAME, applicationMap));
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Failed to connect to %s", url), e);
                }
            }
        }
    }
}
