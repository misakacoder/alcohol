package com.vermouth.serializer;

import com.vermouth.spi.ServiceLoader;

import java.util.List;

public class SerializerFactory {

    private static final String SERIALIZER_NAME_KEY = "vermouth.serializer";
    private static final String DEFAULT_SERIALIZER_NAME = JavaSerializer.class.getName();

    public static Serializer get() {
        return SerializerHolder.serializer;
    }

    private static class SerializerHolder {

        private static final Serializer serializer = create();

        private static Serializer create() {
            String serializerName = System.getProperty(SERIALIZER_NAME_KEY, DEFAULT_SERIALIZER_NAME);
            List<Serializer> serializers = ServiceLoader.load(Serializer.class, null);
            return serializers.stream()
                    .filter(p -> p.getClass().getName().equals(serializerName))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(String.format("Serializer %s not found", serializerName)));
        }
    }
}
