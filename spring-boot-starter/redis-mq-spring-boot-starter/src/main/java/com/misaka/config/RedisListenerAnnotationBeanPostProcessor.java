package com.misaka.config;

import com.misaka.annotation.RedisListener;
import com.misaka.mq.SubscriptionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;

public class RedisListenerAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Autowired
    private ConfigurableApplicationContext configurableApplicationContext;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RedisListener.class)) {
                RedisListener redisListener = method.getAnnotation(RedisListener.class);
                String listenerBeanName = redisListener.topic() + "RedisListener";
                BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SubscriptionFactoryBean.class);
                beanDefinitionBuilder.addConstructorArgValue(redisListener);
                beanDefinitionBuilder.addConstructorArgValue(bean);
                beanDefinitionBuilder.addConstructorArgValue(method);
                BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
                beanFactory.registerBeanDefinition(listenerBeanName, beanDefinition);
                configurableApplicationContext.getBean(listenerBeanName);
            }
        }
        return bean;
    }
}
