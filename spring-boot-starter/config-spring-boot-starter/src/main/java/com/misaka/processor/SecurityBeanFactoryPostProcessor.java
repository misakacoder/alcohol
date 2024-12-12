package com.misaka.processor;

import com.misaka.env.SecurityPropertySource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

public class SecurityBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {

    private static final String PASSWORD_PROPERTY_NAME = "config.encryptor.password";
    private static final Logger log = LoggerFactory.getLogger(SecurityBeanFactoryPostProcessor.class);

    private final ConfigurableEnvironment environment;

    public SecurityBeanFactoryPostProcessor(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        MutablePropertySources propertySources = environment.getPropertySources();
        String password = environment.getProperty(PASSWORD_PROPERTY_NAME, "");
        if (password.length() == 16) {
            for (PropertySource<?> propertySource : propertySources) {
                if (propertySource instanceof MapPropertySource) {
                    String name = propertySource.getName();
                    SecurityPropertySource securityPropertySource = new SecurityPropertySource(password, name, propertySource);
                    environment.getPropertySources().replace(name, securityPropertySource);
                }
            }
        } else {
            log.warn("Config encryption is disabled because the password length is not equal to 16");
        }

    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
