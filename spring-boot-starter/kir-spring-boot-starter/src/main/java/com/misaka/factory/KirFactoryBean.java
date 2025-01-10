package com.misaka.factory;

import com.kir.decoder.JacksonDecoder;
import com.kir.encoder.JacksonEncoder;
import com.kir.http.Interceptor;
import com.misaka.annotation.InterceptorScope;
import com.misaka.annotation.Kir;
import com.rye.util.StringUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.*;
import java.util.stream.Collectors;

public class KirFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

    private Class<?> objectType;
    private ApplicationContext applicationContext;

    @Override
    public Object getObject() throws Exception {
        Kir kir = objectType.getAnnotation(Kir.class);
        String value = kir.url();
        long timeout = kir.timeout();
        value = applicationContext.getEnvironment().resolvePlaceholders(value);
        if (StringUtil.isBlank(value)) {
            throw new RuntimeException(String.format("The value of the annotation Kir in %s is blank", objectType.getName()));
        }
        Set<Interceptor> interceptors = new LinkedHashSet<>();
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(InterceptorScope.class);
        beans.forEach((k, v) -> {
            InterceptorScope interceptorScope = v.getClass().getAnnotation(InterceptorScope.class);
            if (v instanceof Interceptor) {
                boolean addInterceptor = false;
                if (interceptorScope.scope() == InterceptorScope.Scope.ALL) {
                    addInterceptor = true;
                } else {
                    Class<?>[] kirServices = interceptorScope.services();
                    if (Arrays.stream(kirServices).anyMatch(p -> p == objectType)) {
                        addInterceptor = true;
                    }
                }
                if (addInterceptor) {
                    interceptors.add((Interceptor) v);
                }
            }
        });
        for (Class<? extends Interceptor> interceptor : kir.interceptors()) {
            interceptors.add(applicationContext.getBean(interceptor));
        }
        List<Interceptor> interceptorList = interceptors.stream()
                .sorted((p1, p2) -> getOrder(p1) - getOrder(p2))
                .collect(Collectors.toList());
        return com.kir.http.Kir.builder()
                .url(value)
                .connectTimeout(timeout)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .interceptors(interceptorList)
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

    private int getOrder(Interceptor interceptor) {
        Order order = interceptor.getClass().getAnnotation(Order.class);
        if (order != null) {
            return order.value();
        }
        if (interceptor instanceof Ordered) {
            return ((Ordered) interceptor).getOrder();
        }
        return 0;
    }
}
