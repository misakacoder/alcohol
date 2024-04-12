package com.kir.annotation;

import com.kir.http.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
public @interface RequestMapping {

    String value() default "";

    RequestMethod method() default RequestMethod.GET;

    long timeout() default 0L;
}
