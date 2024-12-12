package com.misaka.config;

import com.misaka.filter.BasicAuthFilter;
import com.misaka.properties.BasicAuthProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

import static com.misaka.config.AuthAutoConfiguration.BASIC_AUTH_ENABLE_PROPERTY_NAME;

@ConditionalOnProperty(value = BASIC_AUTH_ENABLE_PROPERTY_NAME, havingValue = "true")
public class AuthAutoConfiguration {

    public static final String BASIC_AUTH_ENABLE_PROPERTY_NAME = "auth.basic.enabled";

    @Bean
    public BasicAuthProperties basicAuthProperties() {
        return new BasicAuthProperties();
    }

    @Bean
    public Filter basicAuthFilter() {
        return new BasicAuthFilter();
    }
}
