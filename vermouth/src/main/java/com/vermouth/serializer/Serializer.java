package com.vermouth.serializer;

import java.io.Serializable;

public interface Serializer {

    byte[] serialize(Serializable object);

    <T> T deserialize(byte[] data);

    <T> T deserialize(byte[] data, Class<T> cls);
}
