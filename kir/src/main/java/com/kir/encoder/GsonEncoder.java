package com.kir.encoder;

import com.google.gson.Gson;

public class GsonEncoder implements Encoder {

    private final Gson gson = new Gson();

    @Override
    public String encode(Object object) {
        return gson.toJson(object);
    }
}
