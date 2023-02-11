package com.gin.cache;

import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RedisCache implements Cache {

    private static RedisTemplate<String, Object> redisTemplate;

    private final String id;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        RedisCache.redisTemplate = redisTemplate;
    }

    public RedisCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        redisTemplate.opsForHash().put(id, key.toString(), value);
    }

    @Override
    public Object getObject(Object key) {
        return redisTemplate.opsForHash().get(id, key.toString());
    }

    @Override
    public Object removeObject(Object key) {
        return redisTemplate.opsForHash().delete(id, key.toString());
    }

    @Override
    public void clear() {
        redisTemplate.delete(id);
    }

    @Override
    public int getSize() {
        Long dbSize = getRedisConnection().dbSize();
        return dbSize != null ? dbSize.intValue() : 0;
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    private RedisConnection getRedisConnection() {
        RedisConnectionFactory redisConnectionFactory = redisTemplate.getConnectionFactory();
        Assert.notNull(redisConnectionFactory, "RedisConnectionFactory is null");
        return redisConnectionFactory.getConnection();
    }

}
