package com.misaka.factory;

import com.kir.decoder.JacksonDecoder;
import com.kir.encoder.JacksonEncoder;
import com.misaka.annotation.Kir;
import com.rye.util.StringUtil;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class KirClientFactoryBean implements FactoryBean<Object>, EnvironmentAware {

    private Class<?> objectType;

    private Environment environment;

    @Override
    public Object getObject() throws Exception {
        Kir kirClient = objectType.getAnnotation(Kir.class);
        String value = kirClient.value();
        long timeout = kirClient.timeout();
        value = environment.resolvePlaceholders(value);
        if (StringUtil.isBlank(value)) {
            throw new RuntimeException(String.format("The value of the annotation KirClient in %s is blank", objectType.getName()));
        }
        return com.kir.http.Kir.builder()
                .url(value)
                .connectTimeout(timeout)
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
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
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
