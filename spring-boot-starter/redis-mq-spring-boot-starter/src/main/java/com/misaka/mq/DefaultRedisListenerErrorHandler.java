package com.misaka.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRedisListenerErrorHandler implements RedisListenerErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(DefaultRedisListenerErrorHandler.class);

    @Override
    public void handle(Object data, Exception e) {
        log.error("", e);
    }
}
