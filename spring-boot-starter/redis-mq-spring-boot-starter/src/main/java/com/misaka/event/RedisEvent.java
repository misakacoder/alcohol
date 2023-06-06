package com.misaka.event;

import org.springframework.util.Assert;

import java.io.Serializable;

public abstract class RedisEvent implements Serializable {

    private final Object source;

    public RedisEvent(Object source) {
        Assert.notNull(source, "null source");
        this.source = source;
    }

    public Object getSource() {
        return source;
    }
}
