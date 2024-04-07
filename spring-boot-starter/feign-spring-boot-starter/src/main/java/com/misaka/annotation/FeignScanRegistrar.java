package com.misaka.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

public class FeignScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(FeignScan.class.getName()));
        String[] basePackages = attributes.getStringArray("basePackages");
        ClassPathFeignScanner scanner = new ClassPathFeignScanner(registry);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Feign.class));
        scanner.doScan(basePackages);
    }
}