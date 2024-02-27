package com.misaka.config;

import org.springframework.data.redis.serializer.RedisSerializer;

public interface Serializer {

    RedisSerializer<String> KEY_SERIALIZER = RedisSerializer.string();

    RedisSerializer<Object> VALUE_SERIALIZER = RedisSerializer.java();
}
