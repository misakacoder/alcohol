package com.misaka.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.misaka.serializer.MaskFieldSerializer;
import com.misaka.enums.MaskType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = MaskFieldSerializer.class)
public @interface MaskField {

    MaskType value() default MaskType.ALL_MASK;

    String character() default "*";
}
