package com.misaka.annotation;

import com.misaka.mq.DefaultRedisListenerErrorHandler;
import com.misaka.mq.RedisListenerErrorHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RedisListener {

    String topic();

    Class<? extends RedisListenerErrorHandler> errorHandler() default DefaultRedisListenerErrorHandler.class;
}
