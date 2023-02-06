package com.misaka.factory;

import com.kir.decoder.JacksonDecoder;
import com.kir.encoder.JacksonEncoder;
import com.kir.http.Kir;
import com.rye.util.StringUtil;
import com.misaka.annotation.KirClient;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KirClientFactoryBean implements FactoryBean<Object>, EnvironmentAware {

    private static final Pattern PROPERTY_NAME_PATTERN = Pattern.compile("\\$\\{(.*?)}");

    private Class<?> objectType;

    private Environment environment;

    @Override
    public Object getObject() throws Exception {
        KirClient kirClient = objectType.getAnnotation(KirClient.class);
        String value = kirClient.value();
        long timeout = kirClient.timeout();
        Matcher matcher = PROPERTY_NAME_PATTERN.matcher(value);
        if (matcher.find()) {
            value = environment.getProperty(matcher.group(1));
        }
        if (StringUtil.isBlank(value)) {
            throw new RuntimeException(String.format("The value of the annotation KirClient in %s is blank", objectType.getName()));
        }
        return Kir.builder()
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
