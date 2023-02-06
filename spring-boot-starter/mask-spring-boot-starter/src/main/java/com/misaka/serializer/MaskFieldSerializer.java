package com.misaka.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.misaka.annotation.MaskField;

import java.io.IOException;

public class MaskFieldSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private MaskField maskField;

    @Override
    public void serialize(String value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        String content = maskField.value().operation().mask(value, maskField.character());
        generator.writeString(content);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        MaskField maskField = beanProperty.getAnnotation(MaskField.class);
        if (maskField != null) {
            this.maskField = maskField;
            return this;
        }
        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}
