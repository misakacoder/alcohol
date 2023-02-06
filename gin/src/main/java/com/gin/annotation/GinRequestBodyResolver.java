package com.gin.annotation;

import cn.hutool.core.io.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMessageConverterMethodArgumentResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GinRequestBodyResolver extends AbstractMessageConverterMethodArgumentResolver {

    private static final Logger log = LoggerFactory.getLogger(GinRequestBodyResolver.class);

    public GinRequestBodyResolver(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(GinRequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return readWithMessageConverters(nativeWebRequest, methodParameter, methodParameter.getParameterType());
    }

    @Override
    protected <T> Object readWithMessageConverters(NativeWebRequest nativeWebRequest, MethodParameter methodParameter, Type targetType) throws IOException, HttpMediaTypeNotSupportedException, HttpMessageNotReadableException {
        log.info("@GinRequestBody before body read");
        HttpInputMessage inputMessage = this.createInputMessage(nativeWebRequest);
        inputMessage = wrapperMessage(inputMessage.getHeaders(), IoUtil.read(inputMessage.getBody(), StandardCharsets.UTF_8));
        return this.readWithMessageConverters(inputMessage, methodParameter, targetType);
    }

    private HttpInputMessage wrapperMessage(HttpHeaders httpHeaders, String message) {
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() {
                return new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpHeaders;
            }
        };
    }
}
