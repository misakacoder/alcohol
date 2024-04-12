package com.misaka.annotation;

import com.kir.http.Interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Kir {

    String url();

    long timeout() default 0L;

    Class<? extends Interceptor>[] interceptors() default {};
}
