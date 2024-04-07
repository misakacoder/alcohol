package com.misaka.annotation;

import feign.*;
import feign.codec.Decoder;
import feign.codec.Encoder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Feign {

    String url() default "";

    Class<? extends Request.Options> options() default Request.Options.class;

    Class<? extends Retryer> retryer() default Retryer.class;

    Class<? extends Contract> contract() default Contract.class;

    Class<? extends Encoder> encoder() default Encoder.class;

    Class<? extends Decoder> decoder() default Decoder.class;

    Class<? extends Logger> logger() default Logger.class;

    Class<? extends RequestInterceptor>[] requestInterceptors() default {};

    Class<? extends ResponseInterceptor>[] responseInterceptors() default {};
}
