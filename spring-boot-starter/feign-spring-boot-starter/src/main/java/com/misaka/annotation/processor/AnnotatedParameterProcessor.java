package com.misaka.annotation.processor;

import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public interface AnnotatedParameterProcessor {

    Class<? extends Annotation> getAnnotationType();

    boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation);

    interface AnnotatedParameterContext {

        MethodMetadata getMethodMetadata();

        int getParamIndex();

        void setParameterName(String name);

        Collection<String> setTemplateParameter(String name, Collection<String> params);
    }
}