package ${packageName}.${factoryPackageName};

import ${packageName}.${basePackageName}.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * <p>
 * 前端映射枚举工厂
 * </p>
 *
 * @author ${author}
 * @date ${date}
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