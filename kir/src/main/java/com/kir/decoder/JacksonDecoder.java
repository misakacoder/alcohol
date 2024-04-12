package com.kir.decoder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;

public class JacksonDecoder implements Decoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Object decode(String data, Method method) {
        try {
            return objectMapper.readValue(data, objectMapper.constructType(method.getGenericReturnType()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
