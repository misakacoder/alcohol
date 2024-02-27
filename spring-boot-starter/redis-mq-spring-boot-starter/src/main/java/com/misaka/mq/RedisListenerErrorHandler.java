package com.misaka.mq;

public interface RedisListenerErrorHandler {
    void handle(Object data, Exception e);
}
