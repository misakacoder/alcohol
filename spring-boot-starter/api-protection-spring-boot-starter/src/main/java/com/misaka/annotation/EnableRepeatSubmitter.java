package com.misaka.annotation;

import com.misaka.aop.RepeatSubmitterAop;
import com.misaka.config.RedisConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({RedisConfig.class, RepeatSubmitterAop.class})
public @interface EnableRepeatSubmitter {

}
