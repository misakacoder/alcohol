package com.kir.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class JacksonDecoder implements Decoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object decode(String data, Method method) {
        try {
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) returnType;
                Type rawType = parameterizedType.getRawType();
                Class<?> rawTypeClass = (Class<?>) rawType;
                Class<?>[] actualTypeClasses = Arrays.stream(parameterizedType.getActualTypeArguments()).map(p -> (Class<?>) p).toArray(Class[]::new);
                return objectMapper.readValue(data, objectMapper.getTypeFactory().constructParametricType(rawTypeClass, actualTypeClasses));
            }
            return objectMapper.readValue(data, method.getReturnType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
