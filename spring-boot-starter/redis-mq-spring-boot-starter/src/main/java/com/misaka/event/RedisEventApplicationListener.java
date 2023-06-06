package com.misaka.event;

public interface RedisEventApplicationListener<E> {
    void onApplicationEvent(E event);
}
