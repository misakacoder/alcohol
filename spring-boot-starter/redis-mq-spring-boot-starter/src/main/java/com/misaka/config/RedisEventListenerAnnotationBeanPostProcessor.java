package com.misaka.config;

import com.misaka.annotation.RedisEventListener;
import com.misaka.event.RedisEventApplicationListener;
import com.misaka.event.RedisEventListenerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class RedisEventListenerAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private RedisEventListenerFactory redisEventListenerFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof RedisEventApplicationListener) {
            redisEventListenerFactory.registerEventListener((RedisEventApplicationListener<Object>) bean);
        }
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RedisEventListener.class)) {
                redisEventListenerFactory.registerEventListener(bean, method);
            }
        }
        return bean;
    }
}
