package com.gin.config;

import cn.hutool.core.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ExecutorConfig {

    private static final Logger log = LoggerFactory.getLogger(ExecutorConfig.class);

    private static final String THREAD_POOL_NAME = "gin-thread-pool-executor";
    private static final String SCHEDULED_THREAD_POOL_NAME = "gin-scheduled-thread-pool-executor";

    @Bean
    public Executor executor() {
        return new ThreadPoolExecutor(
                8,
                8,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(64),
                new NamedThreadFactory(THREAD_POOL_NAME + "-", true),
                (runnable, poolExecutor) -> log.error("The {} is full, reject this task", THREAD_POOL_NAME));
    }

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(
                8,
                new NamedThreadFactory(SCHEDULED_THREAD_POOL_NAME + "-", true),
                (runnable, poolExecutor) -> log.error("The {} is full, reject this task", SCHEDULED_THREAD_POOL_NAME));
    }
}
