package com.misaka.config;

import com.misaka.processor.SecurityBeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;

public class BeanAutoConfiguration {

    @Bean
    public static SecurityBeanFactoryPostProcessor securityBeanFactoryPostProcessor(ConfigurableEnvironment environment) {
        return new SecurityBeanFactoryPostProcessor(environment);
    }
}
