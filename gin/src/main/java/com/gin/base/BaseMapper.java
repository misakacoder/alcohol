package com.gin.base;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper基类
 * </p>
 *
 * @author sherry
 * @date 2022-10-27
 */
public interface BaseMapper<T> {

    Long insert(T entity);

    Long insertSelective(T entity);

    Long insertList(@Param("entityList") List<T> entityList);

    Long deleteByPrimaryKey(Object primaryKey);

    Long delete(T entity);

    Long updateByPrimaryKey(T entity);

    Long updateByPrimaryKeySelective(T entity);

    T selectByPrimaryKey(Object primaryKey);

    T selectOne(T entity);

    List<T> select(T entity);

    List<T> selectAll();

    Long count(T entity);

    <P extends BaseList> List<T> list(P param);

    <P extends BasePage> List<T> page(P param);
}