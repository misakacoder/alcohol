package com.bourbon.config;

import com.bourbon.env.OriginTrackedYamlLoader;
import com.bourbon.properties.BourbonProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConfigLoader {

    private static final String CONFIG_EXT = ".yml";
    private static final String APPLICATION_CONFIG_NAME = "application" + CONFIG_EXT;

    @Autowired
    private BourbonProperties bourbonProperties;

    public Map<String, Object> load(String filename, Boolean isAppName, List<String> profiles) {
        Map<String, Object> configMap = new HashMap<>();
        String searchLocations = bourbonProperties.getSearchLocation();
        if (StringUtils.hasText(searchLocations)) {
            searchLocations = !searchLocations.endsWith(File.separator) ? searchLocations + File.separator : searchLocations;
            List<String> locationList = new ArrayList<>();
            if (isAppName != null && isAppName) {
                locationList.add(searchLocations + APPLICATION_CONFIG_NAME);
            }
            locationList.add(searchLocations + filename + CONFIG_EXT);
            String finalSearchLocations = searchLocations;
            profiles.stream().map(p -> String.format("%s%s-%s%s", finalSearchLocations, filename, p, CONFIG_EXT)).forEach(locationList::add);
            OriginTrackedYamlLoader yamlLoader = new OriginTrackedYamlLoader();
            yamlLoader.setResources(createResources(locationList));
            return yamlLoader.loadAsMap();
        }
        return configMap;
    }

    private Resource[] createResources(List<String> locations) {
        List<Resource> resourceList = new ArrayList<>();
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        for (String location : locations) {
            Resource resource = resourceLoader.getResource(location);
            if (resource.exists()) {
                resourceList.add(resource);
            }
        }
        return resourceList.toArray(new Resource[0]);
    }
}
