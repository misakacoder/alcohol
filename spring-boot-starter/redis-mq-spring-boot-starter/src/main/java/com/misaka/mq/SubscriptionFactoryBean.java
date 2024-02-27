package com.misaka.mq;

import com.misaka.annotation.RedisListener;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.lang.reflect.Method;

public class SubscriptionFactoryBean implements FactoryBean<Subscription> {

    private final ReadOffset READ_FROM_BEGIN = ReadOffset.from("0-0");

    private final String stream;

    private final Class<? extends RedisListenerErrorHandler> errorHandler;

    private final Object bean;

    private final Method method;

    @Value("${spring.redis.mq.group-name:stream}")
    private String groupName;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StreamMessageListenerContainer<String, ObjectRecord<String, RedisRecord>> streamMessageListenerContainer;


    public SubscriptionFactoryBean(RedisListener redisListener, Object bean, Method method) {
        this.stream = redisListener.topic();
        this.errorHandler = redisListener.errorHandler();
        this.bean = bean;
        this.method = method;
    }

    @Override
    public Subscription getObject() throws Exception {
        createGroup();
        return streamMessageListenerContainer.receiveAutoAck(
                Consumer.from(groupName, "consumer"),
                StreamOffset.create(stream, ReadOffset.lastConsumed()),
                getStreamListener()
        );
    }

    @Override
    public Class<?> getObjectType() {
        return Subscription.class;
    }

    private void createGroup() {
        try {
            //ReadOffset.from("0-0"): 从指定id开始消费
            //ReadOffset.latest(): 从尾部开始消费，只接受新消息
            redisTemplate.opsForStream().createGroup(stream, READ_FROM_BEGIN, groupName);
        } catch (Exception ignored) {
            //重复创建group会报错
        }
    }

    public StreamListener<String, ObjectRecord<String, RedisRecord>> getStreamListener() {
        return objectRecord -> {
            Object object = objectRecord.getValue().getObject();
            try {
                method.invoke(bean, object);
            } catch (Exception e) {
                applicationContext.getBeansOfType(errorHandler).forEach((k, v) -> v.handle(object, e));
            }
        };
    }
}
