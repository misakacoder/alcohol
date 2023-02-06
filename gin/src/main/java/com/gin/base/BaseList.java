package com.gin.base;

/**
 * <p>
 * List基类
 * </p>
 *
 * @author sherry
 * @date 2022-10-27
 */
public class BaseList {

    public static final int DEFAULT_LIMIT = 10;

    private Integer limit;

    private String orderBy;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
