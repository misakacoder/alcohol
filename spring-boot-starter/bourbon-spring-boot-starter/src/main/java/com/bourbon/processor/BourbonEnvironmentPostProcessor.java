package com.bourbon.processor;

import com.bourbon.consts.BourbonConsts;
import com.bourbon.env.OriginTrackedYamlLoader;
import com.bourbon.properties.BourbonProperties;
import com.bourbon.util.BourbonUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Order(Integer.MIN_VALUE)
public class BourbonEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String CONFIG_NAME = "bootstrap.yml";

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
        Binder binder = Binder.get(environment);
        List<String> imports = binder.bind(BourbonConsts.CONFIG_IMPORT_PROPERTY_NAME, Bindable.listOf(String.class)).orElseGet(ArrayList::new);
        if (CollectionUtils.isEmpty(imports)) {
            BourbonProperties bourbonProperties = binder.bind("bourbon", Bindable.of(BourbonProperties.class)).orElseGet(BourbonProperties::new);
            String appName = environment.getProperty(BourbonConsts.APPLICATION_NAME_PROPERTY_NAME);
            String profile = environment.getProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME);
            MapPropertySource propertySource = BourbonUtil.pullConfig(bourbonProperties, appName, true, profile);
            if (propertySource != null) {
                environment.getPropertySources().addLast(propertySource);
            }
        }
    }
}
