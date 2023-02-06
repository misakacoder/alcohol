package com.misaka.env;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.misaka.util.MapUtil;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JsonPropertySourceLoader implements PropertySourceLoader {

    @Override
    public String[] getFileExtensions() {
        return new String[]{"json"};
    }

    @Override
    public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        try (ReadableByteChannel channel = resource.readableChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate((int) resource.contentLength());
            channel.read(buffer);
            String jsonString = new String(buffer.array(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> configMap = objectMapper.readValue(jsonString, new TypeReference<>() {
            });
            Map<String, Object> flattenedMap = MapUtil.getFlattenedMap(configMap);
            return Collections.singletonList(new MapPropertySource(name, flattenedMap));
        }
    }
}
