package com.gin.handler;

import com.gin.base.BaseEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>
 * 数据库映射枚举处理器
 * </p>
 *
 * @author sherry
 * @date 2022-10-27
 */
public class BaseEnumTypeHandler<E extends BaseEnum<T>, T> extends BaseTypeHandler<E> {

    private final E[] enums;

    public BaseEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null");
        }
        enums = type.getEnumConstants();
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E e, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, e.getValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getEnumByValue(rs.getObject(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int i) throws SQLException {
        return getEnumByValue(rs.getObject(i));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int i) throws SQLException {
        return getEnumByValue(cs.getObject(i));
    }

    private E getEnumByValue(Object value) {
        for (E e : enums) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        throw new IllegalArgumentException("Enum object not exist");
    }
}