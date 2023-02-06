package com.kir.annotation;

import com.kir.http.RequestMethod;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@RequestMapping(method = RequestMethod.DELETE)
public @interface DeleteMapping {

    String value();

    long timeout() default 0L;
}
