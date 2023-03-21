package com.korn.action;

public class PropertiesAction extends BaseAction {

    @Override
    public String getValue(String key) {
        return System.getProperty(key);
    }
}
