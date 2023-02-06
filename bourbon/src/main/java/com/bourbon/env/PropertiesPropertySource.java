package com.bourbon.env;

import org.springframework.core.env.PropertySource;

import java.util.Properties;

public class PropertiesPropertySource extends PropertySource<Properties> {

    public PropertiesPropertySource(String name, Properties properties) {
        super(name, properties);
    }

    @Override
    public Object getProperty(String name) {
        return source.get(name);
    }
}
