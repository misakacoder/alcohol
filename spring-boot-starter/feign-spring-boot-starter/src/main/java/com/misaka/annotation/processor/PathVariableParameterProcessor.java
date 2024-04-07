package com.misaka.annotation.processor;

import feign.MethodMetadata;
import feign.Util;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public class PathVariableParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<PathVariable> ANNOTATION = PathVariable.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation) {
        MethodMetadata methodMetadata = context.getMethodMetadata();
        String name = ANNOTATION.cast(annotation).value();
        Util.checkState(Util.emptyToNull(name) != null, "PathVariable annotation was empty on param %s.", context.getParamIndex());
        context.setParameterName(name);
        String varName = '{' + name + '}';
        String varNameRegex = ".*\\{" + name + "(:[^}]+)?\\}.*";
        if (!methodMetadata.template().url().matches(varNameRegex) && !containsMapValues(methodMetadata.template().queries(), varName) && !containsMapValues(methodMetadata.template().headers(), varName)) {
            methodMetadata.formParams().add(name);
        }
        return true;
    }

    private <K, V> boolean containsMapValues(Map<K, Collection<V>> map, V value) {
        Collection<Collection<V>> values = map.values();
        if (values == null) {
            return false;
        }
        for (Collection<V> entry : values) {
            if (entry.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
