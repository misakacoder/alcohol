package com.sherry.model;

public class Column {

    private String name;

    private Boolean needAnnotation;

    private String type;

    private Boolean key;

    private Boolean incr;

    private String fieldName;

    private String upperFieldName;

    private String jdbcType;

    private String javaType;

    private Boolean enumType;

    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNeedAnnotation() {
        return needAnnotation;
    }

    public void setNeedAnnotation(Boolean needAnnotation) {
        this.needAnnotation = needAnnotation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getKey() {
        return key;
    }

    public void setKey(Boolean key) {
        this.key = key;
    }

    public Boolean getIncr() {
        return incr;
    }

    public void setIncr(Boolean incr) {
        this.incr = incr;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getUpperFieldName() {
        return upperFieldName;
    }

    public void setUpperFieldName(String upperFieldName) {
        this.upperFieldName = upperFieldName;
    }

    public String getJdbcType() {
        return jdbcType;
    }

    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public Boolean getEnumType() {
        return enumType;
    }

    public void setEnumType(Boolean enumType) {
        this.enumType = enumType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
