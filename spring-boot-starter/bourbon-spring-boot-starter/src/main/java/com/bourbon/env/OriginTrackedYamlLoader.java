package com.bourbon.env;

import org.springframework.beans.factory.config.YamlProcessor;

import java.util.*;

public class OriginTrackedYamlLoader extends YamlProcessor {

    public Properties loadAsProperties() {
        Properties properties = new Properties();
        process((prop, map) -> properties.putAll(prop));
        return properties;
    }

    public List<Properties> loadAsPropertiesList() {
        List<Properties> propertiesList = new ArrayList<>();
        process((prop, map) -> propertiesList.add(prop));
        return propertiesList;
    }

    public Map<String, Object> loadAsMap() {
        Map<String, Object> dataMap = new HashMap<>();
        process((properties, map) -> dataMap.putAll(getFlattenedMap(map)));
        return dataMap;
    }

    public List<Map<String, Object>> loadAsMapList() {
        List<Map<String, Object>> dataMapList = new ArrayList<>();
        process((properties, map) -> dataMapList.add(getFlattenedMap(map)));
        return dataMapList;
    }
}
