package com.kir.decoder;

import com.google.gson.Gson;

import java.lang.reflect.Method;

public class GsonDecoder implements Decoder {

    private final Gson gson = new Gson();

    @Override
    public Object decode(String data, Method method) {
        return gson.fromJson(data, method.getGenericReturnType());
    }
}
