package com.misaka.context;

import com.misaka.event.RedisEvent;
import com.misaka.mq.RedisRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisApplicationContext {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void publishEvent(Object event) {
        redisTemplate.convertAndSend(event.getClass().getName(), event);
    }

    public void publishEvent(RedisEvent event) {
        this.publishEvent((Object) event);
    }

    public void send(String topic, Object data) {
        redisTemplate.opsForStream().add(Record.of(RedisRecord.of(data)).withStreamKey(topic));
    }
}
