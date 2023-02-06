package com.kir.decoder;

import com.rye.util.StringUtil;

import java.lang.reflect.Method;
import java.math.BigDecimal;

public interface Decoder {

    static Object decode(String data, Method method, Decoder decoder) {
        Class<?> returnType = method.getReturnType();
        if (StringUtil.isBlank(data) || returnType == void.class) {
            return null;
        }
        if (returnType == String.class) {
            return data;
        } else if (returnType == boolean.class || Boolean.class.isAssignableFrom(returnType)) {
            return Boolean.valueOf(data);
        } else if (returnType == char.class || Character.class.isAssignableFrom(returnType)) {
            return data.charAt(0);
        } else if (returnType.isPrimitive() || Number.class.isAssignableFrom(returnType)) {
            BigDecimal number = new BigDecimal(data);
            if (returnType == float.class || Float.class.isAssignableFrom(returnType)) {
                return number.floatValue();
            } else if (returnType == double.class || Double.class.isAssignableFrom(returnType)) {
                return number.doubleValue();
            } else if (returnType == byte.class || Byte.class.isAssignableFrom(returnType)) {
                return number.byteValue();
            } else if (returnType == short.class || Short.class.isAssignableFrom(returnType)) {
                return number.shortValue();
            } else if (returnType == int.class || Integer.class.isAssignableFrom(returnType)) {
                return number.intValue();
            } else if (returnType == long.class || Long.class.isAssignableFrom(returnType)) {
                return number.longValue();
            } else {
                return number;
            }
        } else {
            return decoder.decode(data, method);
        }
    }

    Object decode(String data, Method method);
}
