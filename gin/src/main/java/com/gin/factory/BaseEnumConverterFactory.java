package com.gin.factory;

import com.gin.base.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * <p>
 * 前端映射枚举工厂
 * </p>
 *
 * @author sherry
 * @date 2022-10-27
 */
public class BaseEnumConverterFactory implements ConverterFactory<String, BaseEnum<?>> {

    @Override
    public <E extends BaseEnum<?>> Converter<String, E> getConverter(Class<E> cls) {
        return value -> {
            for (E e : cls.getEnumConstants()) {
                if (e.getValue().toString().equals(value) || e.getLabel().equals(value)) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Enum object not exist");
        };
    }
}