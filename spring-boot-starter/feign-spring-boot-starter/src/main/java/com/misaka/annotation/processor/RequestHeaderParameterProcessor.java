package com.misaka.annotation.processor;

import feign.MethodMetadata;
import feign.Util;
import org.springframework.web.bind.annotation.RequestHeader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class RequestHeaderParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestHeader> ANNOTATION = RequestHeader.class;

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
            Util.checkState(methodMetadata.headerMapIndex() == null, "Header map can only be present once.");
            methodMetadata.headerMapIndex(paramIndex);
            return true;
        }
        String name = ANNOTATION.cast(annotation).value();
        Util.checkState(Util.emptyToNull(name) != null, "RequestHeader.value() was empty on parameter %s", paramIndex);
        context.setParameterName(name);
        Collection<String> header = context.setTemplateParameter(name, methodMetadata.template().headers().get(name));
        methodMetadata.template().header(name, header);
        return true;
    }
}
