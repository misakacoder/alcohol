package com.vermouth.persistence;

import com.vermouth.serializer.SerializerFactory;
import org.apache.commons.io.FileUtils;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksIterator;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RocksDBRepository<K extends Serializable, V extends Serializable> implements Repository<K, V> {

    private final RocksDB rocksDB;

    public RocksDBRepository(String path) {
        try (Options options = new Options().setCreateIfMissing(true)) {
            FileUtils.forceMkdir(new File(path));
            rocksDB = RocksDB.open(options, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public V get(K key) {
        try {
            byte[] data = rocksDB.get(serialize(key));
            return data != null ? deserialize(data) : null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(K key, V value) {
        try {
            rocksDB.put(serialize(key), serialize(value));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void del(K key) {
        try {
            rocksDB.delete(serialize(key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object dump() {
        Map<byte[], byte[]> store = new HashMap<>();
        try (RocksIterator iterator = rocksDB.newIterator()) {
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                byte[] key = iterator.key();
                byte[] value = iterator.value();
                store.put(key, value);
            }
        }
        return store;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load(Object object) {
        if (object instanceof Map) {
            try {
                Map<byte[], byte[]> store = (Map<byte[], byte[]>) object;
                for (Map.Entry<byte[], byte[]> entry : store.entrySet()) {
                    rocksDB.put(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private byte[] serialize(Serializable object) {
        return SerializerFactory.get().serialize(object);
    }

    private V deserialize(byte[] data) {
        return SerializerFactory.get().deserialize(data);
    }
}
