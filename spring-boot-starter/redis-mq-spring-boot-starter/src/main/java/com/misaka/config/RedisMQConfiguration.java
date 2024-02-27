package com.misaka.config;

import com.misaka.context.RedisApplicationContext;
import com.misaka.event.RedisEventListenerFactory;
import com.misaka.mq.DefaultRedisListenerErrorHandler;
import com.misaka.mq.RedisRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisMQConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(Serializer.KEY_SERIALIZER);
        redisTemplate.setHashKeySerializer(Serializer.KEY_SERIALIZER);
        redisTemplate.setValueSerializer(Serializer.VALUE_SERIALIZER);
        redisTemplate.setHashValueSerializer(Serializer.VALUE_SERIALIZER);
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory, @Qualifier("redisEventExecutor") Executor executor) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setTaskExecutor(executor);
        container.setConnectionFactory(connectionFactory);
        return container;
    }

    @Bean
    public StreamMessageListenerContainer<String, ObjectRecord<String, RedisRecord>> streamMessageListenerContainer(RedisConnectionFactory redisConnectionFactory, RedisTemplate<String, Object> redisTemplate, @Qualifier("redisMessageQueueExecutor") Executor executor) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, RedisRecord>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .batchSize(100)
                        .executor(executor)
                        .keySerializer(Serializer.KEY_SERIALIZER)
                        .hashKeySerializer(Serializer.KEY_SERIALIZER)
                        .hashValueSerializer(Serializer.VALUE_SERIALIZER)
                        .objectMapper(redisTemplate.opsForStream().getHashMapper(RedisRecord.class))
                        .targetType(RedisRecord.class)
                        .build();
        StreamMessageListenerContainer<String, ObjectRecord<String, RedisRecord>> streamMessageListenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);
        streamMessageListenerContainer.start();
        return streamMessageListenerContainer;
    }

    @Bean("redisEventExecutor")
    public Executor redisEventExecutor() {
        return new ThreadPoolExecutor(
                16,
                16,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                namedThreadFactory("redis-event-executor-%s")
        );
    }

    @Bean("redisMessageQueueExecutor")
    public Executor redisMessageQueueExecutor() {
        return new ThreadPoolExecutor(
                16,
                16,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                namedThreadFactory("redis-mq-executor-%s")
        );
    }

    @Bean
    public RedisEventListenerFactory redisEventListenerFactory() {
        return new RedisEventListenerFactory();
    }

    @Bean
    public DefaultRedisListenerErrorHandler defaultListenerErrorHandler() {
        return new DefaultRedisListenerErrorHandler();
    }

    @Bean
    public RedisApplicationContext redisApplicationContext() {
        return new RedisApplicationContext();
    }

    @Bean
    public static RedisEventListenerAnnotationBeanPostProcessor redisEventListenerBeanPostProcessor() {
        return new RedisEventListenerAnnotationBeanPostProcessor();
    }

    @Bean
    public static RedisListenerAnnotationBeanPostProcessor redisListenerAnnotationBeanPostProcessor() {
        return new RedisListenerAnnotationBeanPostProcessor();
    }

    private ThreadFactory namedThreadFactory(String nameFormat) {
        return new ThreadFactory() {

            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setName(String.format(nameFormat, threadNumber.getAndIncrement()));
                return thread;
            }
        };
    }
}
