package com.misaka.annotation;

import com.misaka.factory.KirClientFactoryBean;
import com.rye.util.ClassUtil;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;
import java.util.Set;

public class KirClientsBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("", KirClient.class);
        for (Class<?> cls : classes) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(KirClientFactoryBean.class);
            beanDefinition.getPropertyValues().add("objectType", cls);
            //AUTOWIRE_NO: 默认的装配模式，这种方式不能进行自动注入，需使用@Resource或@Autowired注解手动注入
            //AUTOWIRE_BY_NAME: 通过属性的名称来自动注入，未找到bean则不会注入。需要提供set方法，因为是通过set方法来赋值的
            //AUTOWIRE_BY_TYPE: 通过属性的类型来自动注入，未找到bean则不会注入，若找到多个bean则抛出异常。需要提供set方法，因为是通过set方法来赋值的
            //AUTOWIRE_CONSTRUCTOR: 通过构造器自动注入，查找bean是通过类型来查找的
            beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_NO);
            registry.registerBeanDefinition(generateBeanName(cls), beanDefinition);
        }
    }

    private String generateBeanName(Class<?> beanType) {
        String shortClassName = ClassUtils.getShortName(beanType.getName());
        return Introspector.decapitalize(shortClassName);
    }
}