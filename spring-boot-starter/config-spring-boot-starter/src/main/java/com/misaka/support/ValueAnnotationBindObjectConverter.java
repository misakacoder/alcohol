package com.misaka.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

import java.util.Collections;
import java.util.Set;

public class ValueAnnotationBindObjectConverter implements ConditionalGenericConverter {

    private static final String BIND_SUPPORT_PREFIX = "object:";

    private final Binder binder;

    public ValueAnnotationBindObjectConverter(Binder binder) {
        this.binder = binder;
    }

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        Value value = targetType.getAnnotation(Value.class);
        return value != null && value.value().startsWith(BIND_SUPPORT_PREFIX);
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Object.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Value value = targetType.getAnnotation(Value.class);
        Class<?> cls = targetType.getType();
        return binder.bind(value.value().replace(BIND_SUPPORT_PREFIX, ""), Bindable.of(cls)).get();
    }
}
