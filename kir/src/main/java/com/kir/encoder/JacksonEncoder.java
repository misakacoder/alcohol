package com.kir.encoder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonEncoder implements Encoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String encode(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
