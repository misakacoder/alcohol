package com.misaka.support;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

public class FeignFactoryBean<T> implements FactoryBean<T>, EnvironmentAware, ApplicationContextAware {

    private final Class<T> objectType;
    private Environment environment;
    private ApplicationContext applicationContext;

    public FeignFactoryBean(Class<T> objectType) {
        this.objectType = objectType;
    }

    @Override
    public T getObject() throws Exception {
        com.misaka.annotation.Feign feign = objectType.getAnnotation(com.misaka.annotation.Feign.class);
        String url = environment.resolvePlaceholders(feign.url());
        Request.Options options = applicationContext.getBean(feign.options());
        Retryer retryer = applicationContext.getBean(feign.retryer());
        Contract contract = applicationContext.getBean(feign.contract());
        Encoder encoder = applicationContext.getBean(feign.encoder());
        Decoder decoder = applicationContext.getBean(feign.decoder());
        Logger logger = applicationContext.getBean(feign.logger());
        List<RequestInterceptor> requestInterceptors = new ArrayList<>();
        for (Class<? extends RequestInterceptor> requestInterceptor : feign.requestInterceptors()) {
            requestInterceptors.add(applicationContext.getBean(requestInterceptor));
        }
        List<ResponseInterceptor> responseInterceptors = new ArrayList<>();
        for (Class<? extends ResponseInterceptor> responseInterceptor : feign.responseInterceptors()) {
            responseInterceptors.add(applicationContext.getBean(responseInterceptor));
        }
        return Feign.builder()
                .options(options)
                .retryer(retryer)
                .contract(contract)
                .encoder(encoder)
                .decoder(decoder)
                .logger(logger)
                .logLevel(Logger.Level.FULL)
                .requestInterceptors(requestInterceptors)
                .responseInterceptors(responseInterceptors)
                .target(objectType, url);
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
