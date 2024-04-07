package com.misaka.annotation.processor;

import feign.MethodMetadata;
import feign.Util;
import org.springframework.web.bind.annotation.MatrixVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

public class MatrixVariableParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<MatrixVariable> ANNOTATION = MatrixVariable.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation) {
        MethodMetadata methodMetadata = context.getMethodMetadata();
        int paramIndex = context.getParamIndex();
        Class<?> parameterType = method.getParameterTypes()[paramIndex];
        String name = ANNOTATION.cast(annotation).value();
        Util.checkState(Util.emptyToNull(name) != null, "MatrixVariable annotation was empty on param %s.", context.getParamIndex());
        context.setParameterName(name);
        if (Map.class.isAssignableFrom(parameterType)) {
            methodMetadata.indexToExpander().put(paramIndex, this::expandMap);
        } else {
            methodMetadata.indexToExpander().put(paramIndex, object -> ";" + name + "=" + object.toString());
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private String expandMap(Object object) {
        Map<String, Object> paramMap = (Map<String, Object>) object;
        return paramMap.keySet().stream()
                .filter(key -> paramMap.get(key) != null)
                .map(key -> ";" + key + "=" + paramMap.get(key).toString())
                .collect(Collectors.joining());
    }
}
