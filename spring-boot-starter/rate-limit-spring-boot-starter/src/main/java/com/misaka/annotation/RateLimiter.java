package com.misaka.annotation;

import com.misaka.enums.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimiter {

    String key() default "rate_limit";

    int count() default 100;

    int time() default 60;

    LimitType limitType() default LimitType.GLOBAL;
}
