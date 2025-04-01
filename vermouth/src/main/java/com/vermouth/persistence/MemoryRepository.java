package com.vermouth.persistence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryRepository<K extends Serializable, V extends Serializable> implements Repository<K, V> {

    private final Map<K, V> store = new ConcurrentHashMap<>();

    @Override
    public V get(K key) {
        return store.get(key);
    }

    @Override
    public void put(K key, V value) {
        store.put(key, value);
    }

    @Override
    public void del(K key) {
        store.remove(key);
    }

    @Override
    public Object dump() {
        return new HashMap<>(store);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(Object object) {
        if (object instanceof Map) {
            store.putAll((Map<K, V>) object);
        }
    }
}
