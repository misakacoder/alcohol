package com.misaka.annotation.processor;

import feign.MethodMetadata;
import feign.Util;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.CookieValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;

public class CookieValueParameterProcessor implements AnnotatedParameterProcessor {

    private static final Class<CookieValue> ANNOTATION = CookieValue.class;

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return ANNOTATION;
    }

    @Override
    public boolean processArgument(AnnotatedParameterContext context, Method method, Annotation annotation) {
        MethodMetadata methodMetadata = context.getMethodMetadata();
        int paramIndex = context.getParamIndex();
        CookieValue cookieValue = ANNOTATION.cast(annotation);
        String name = cookieValue.value().trim();
        Util.checkState(Util.emptyToNull(name) != null, "Cookie.name() was empty on parameter %s", paramIndex);
        context.setParameterName(name);
        String cookieExpression = methodMetadata.template().headers().getOrDefault(HttpHeaders.COOKIE, Collections.singletonList("")).stream().findFirst().orElse("");
        if (cookieExpression.isEmpty()) {
            cookieExpression = String.format("%s={%s}", name, name);
        } else {
            cookieExpression += String.format("; %s={%s}", name, name);
        }
        methodMetadata.template().removeHeader(HttpHeaders.COOKIE);
        methodMetadata.template().header(HttpHeaders.COOKIE, cookieExpression);
        return true;
    }

}
