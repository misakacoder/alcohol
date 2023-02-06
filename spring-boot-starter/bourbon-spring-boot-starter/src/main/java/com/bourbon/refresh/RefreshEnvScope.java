package com.bourbon.refresh;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RefreshEnvScope implements Scope {

    public static final String SCOPE = "refresh";
    private final Map<String, Object> beanMap = new ConcurrentHashMap<>();
    private static final RefreshEnvScope INSTANCE = new RefreshEnvScope();

    private RefreshEnvScope() {

    }

    public static RefreshEnvScope instance() {
        return INSTANCE;
    }

    public static void clear() {
        INSTANCE.beanMap.clear();
    }

    @Override
    public Object get(String beanName, ObjectFactory<?> objectFactory) {
        return beanMap.computeIfAbsent(beanName, key -> objectFactory.getObject());
    }

    @Override
    public Object remove(String beanName) {
        return beanMap.remove(beanName);
    }

    @Override
    public void registerDestructionCallback(String beanName, Runnable callback) {

    }

    @Override
    public Object resolveContextualObject(String beanName) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }
}
