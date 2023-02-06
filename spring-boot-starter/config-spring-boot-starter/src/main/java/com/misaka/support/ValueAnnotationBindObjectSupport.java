package com.misaka.support;

import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * ApplicationContextInitializer：spring容器刷新前执行的回调类
 * 支持如下方式(多种方式优先级按顺序排列，同种方式可以使用@Order注解设置执行优先级)
 * 1. 配置文件：context.initializer.classes=com.misaka.properties.ValueAnnotationBindObjectSupport
 * 2. SPI：META-INF/spring.factories
 * 3. main方法：springApplication.addInitializers(new ValueAnnotationBindObjectSupport())
 */
public class ValueAnnotationBindObjectSupport implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        Binder binder = Binder.get(context.getEnvironment());
        ApplicationConversionService conversionService = (ApplicationConversionService) context.getBeanFactory().getConversionService();
        conversionService.addConverter(new ValueAnnotationBindObjectConverter(binder));
    }
}
