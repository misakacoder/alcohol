package com.bourbon.loader;

import com.bourbon.util.BourbonUtil;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.core.env.MapPropertySource;

import java.util.ArrayList;
import java.util.List;

public class BourbonConfigDataLoader implements ConfigDataLoader<BourbonConfigDataResource> {

    @Override
    public ConfigData load(ConfigDataLoaderContext context, BourbonConfigDataResource resource) throws ConfigDataResourceNotFoundException {
        MapPropertySource propertySource = BourbonUtil.pullConfig(resource.getBourbonProperties(), resource.getFilename(), resource.isAppName(), resource.getProfile());
        List<MapPropertySource> propertySourceList = new ArrayList<>();
        if (propertySource != null) {
            propertySourceList.add(propertySource);
        }
        return new ConfigData(propertySourceList);
    }
}
