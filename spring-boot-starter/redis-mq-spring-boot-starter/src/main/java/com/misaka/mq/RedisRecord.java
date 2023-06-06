package com.misaka.mq;

import com.misaka.config.Serializer;

public class RedisRecord {

    private byte[] data;

    public static RedisRecord of(Object data) {
        RedisRecord record = new RedisRecord();
        record.data = Serializer.VALUE_SERIALIZER.serialize(data);
        return record;
    }

    public Object getObject() {
        return Serializer.VALUE_SERIALIZER.deserialize(data);
    }
}
