package com.misaka.annotation.processor;

import feign.MethodMetadata;
import feign.Util;
import org.springframework.web.bind.annotation.RequestPart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

public class RequestPartParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<RequestPart> ANNOTATION = RequestPart.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation) {
        MethodMetadata methodMetadata = context.getMethodMetadata();
        int paramIndex = context.getParamIndex();
        String name = ANNOTATION.cast(annotation).value();
        Util.checkState(Util.emptyToNull(name) != null, "RequestPart.value() was empty on parameter %s", paramIndex);
        context.setParameterName(name);
        methodMetadata.formParams().add(name);
        Collection<String> names = context.setTemplateParameter(name, methodMetadata.indexToName().get(paramIndex));
        methodMetadata.indexToName().put(paramIndex, names);
        return true;
    }
}
