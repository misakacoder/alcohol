package com.misaka.factory;

import com.kir.decoder.JacksonDecoder;
import com.kir.encoder.JacksonEncoder;
import com.kir.http.Interceptor;
import com.misaka.annotation.Kir;
import com.rye.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;

public class KirClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    private Class<?> objectType;
    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        Kir kir = objectType.getAnnotation(Kir.class);
        String value = kir.value();
        long timeout = kir.timeout();
        value = applicationContext.getEnvironment().resolvePlaceholders(value);
        if (StringUtil.isBlank(value)) {
            throw new RuntimeException(String.format("The value of the annotation Kir in %s is blank", objectType.getName()));
        }
        List<Interceptor> interceptors = new ArrayList<>();
        for (Class<? extends Interceptor> interceptor : kir.interceptors()) {
            interceptors.add(applicationContext.getBean(interceptor));
        }
        return com.kir.http.Kir.builder()
                .url(value)
                .connectTimeout(timeout)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .interceptors(interceptors)
                .target(objectType);
    }

    @Override
    public Class<?> getObjectType() {
        return objectType;
    }

    public void setObjectType(Class<?> objectType) {
        this.objectType = objectType;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
