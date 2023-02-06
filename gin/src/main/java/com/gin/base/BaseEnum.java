package com.gin.base;

import java.io.Serializable;

/**
 * <p>
 * 枚举基类
 * </p>
 *
 * @author sherry
 * @date 2022-10-27
 */
public interface BaseEnum<T> extends Serializable {

    T getValue();

    String getLabel();
}