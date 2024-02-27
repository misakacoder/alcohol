package com.misaka.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class RedisEventListenerFactory {

    private static final Logger log = LoggerFactory.getLogger(RedisEventListenerFactory.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    public void registerEventListener(Object bean, Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 1) {
            String channel = parameters[0].getType().getName();
            redisMessageListenerContainer.addMessageListener(messageListener(bean, method), new ChannelTopic(channel));
        } else {
            log.warn("This listener '{}:{}' cannot be registered because there can be only one method parameter", bean.getClass().getName(), method.getName());
        }
    }

    public void registerEventListener(RedisEventApplicationListener<Object> redisEventApplicationListener) {
        String channel = null;
        Type[] types = redisEventApplicationListener.getClass().getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                if (rawType == RedisEventApplicationListener.class) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    channel = actualTypeArguments[0].getTypeName();
                    break;
                }
            }
        }
        if (channel != null) {
            redisMessageListenerContainer.addMessageListener(messageListener(redisEventApplicationListener), new ChannelTopic(channel));
        }
    }

    private MessageListener messageListener(Object bean, Method method) {
        return ((message, bytes) -> {
            try {
                method.invoke(bean, getBody(message));
            } catch (Exception e) {
                log.error("", e);
            }
        });
    }

    private MessageListener messageListener(RedisEventApplicationListener<Object> redisEventApplicationListener) {
        return ((message, bytes) -> redisEventApplicationListener.onApplicationEvent(getBody(message)));
    }

    private Object getBody(Message message) {
        RedisSerializer<?> valueSerializer = redisTemplate.getValueSerializer();
        return valueSerializer.deserialize(message.getBody());
    }
}
