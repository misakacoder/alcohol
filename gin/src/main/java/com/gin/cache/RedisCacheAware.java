package com.gin.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheAware {

    public RedisCacheAware(RedisTemplate<String, Object> redisTemplate) {
        RedisCache.setRedisTemplate(redisTemplate);
    }
}
