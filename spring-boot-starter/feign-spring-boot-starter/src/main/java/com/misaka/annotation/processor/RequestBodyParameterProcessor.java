package com.misaka.annotation.processor;

import feign.MethodMetadata;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class RequestBodyParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestBody> ANNOTATION = RequestBody.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation) {
        MethodMetadata methodMetadata = context.getMethodMetadata();
        int paramIndex = context.getParamIndex();
        methodMetadata.template().header(HttpHeaders.CONTENT_TYPE, "application/json");
        methodMetadata.bodyIndex(paramIndex);
        methodMetadata.bodyType(method.getParameterTypes()[paramIndex]);
        return true;
    }
}
