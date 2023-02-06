package com.rye.util;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Function;

public class ClassUtil {

    public static Set<Class<?>> scanPackage(String packageName) {
        return scanPackage(packageName, null);
    }

    public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        return scanPackage(packageName, cls -> cls.isAnnotationPresent(annotation));
    }

    public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
        return scanPackage(packageName, cls -> !superClass.equals(cls) && superClass.isAssignableFrom(cls));
    }

    public static Set<Class<?>> scanPackage(String packageName, Function<Class<?>, Boolean> classFilter) {
        return new ClassScanner(packageName, classFilter).scan();
    }
}
