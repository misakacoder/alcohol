package com.misaka.annotation.processor;

import feign.MethodMetadata;
import feign.Util;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class RequestParamParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestParam> ANNOTATION = RequestParam.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation) {
        MethodMetadata methodMetadata = context.getMethodMetadata();
        int paramIndex = context.getParamIndex();
        Class<?> parameterType = method.getParameterTypes()[paramIndex];
        if (Map.class.isAssignableFrom(parameterType)) {
            Util.checkState(methodMetadata.queryMapIndex() == null, "Query map can only be present once.");
            methodMetadata.queryMapIndex(paramIndex);
            return true;
        }
        RequestParam requestParam = ANNOTATION.cast(annotation);
        String name = requestParam.value();
        Util.checkState(Util.emptyToNull(name) != null, "RequestParam.value() was empty on parameter %s of method %s", paramIndex, method.getName());
        context.setParameterName(name);
        Collection<String> query = context.setTemplateParameter(name, methodMetadata.template().queries().get(name));
        methodMetadata.template().query(name, query);
        return true;
    }
}
