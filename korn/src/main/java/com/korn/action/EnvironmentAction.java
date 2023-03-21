package com.korn.action;

public class EnvironmentAction extends BaseAction {

    @Override
    public String getValue(String key) {
        return System.getenv(key);
    }
}
