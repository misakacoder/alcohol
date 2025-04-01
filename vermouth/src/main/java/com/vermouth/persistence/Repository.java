package com.vermouth.persistence;

import java.io.Serializable;

public interface Repository<K extends Serializable, V extends Serializable> {

    V get(K key);

    void put(K key, V value);

    void del(K key);

    default Object dump() {
        return null;
    }

    default void load(Object object) {

    }
}
