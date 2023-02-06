package com.sherry.model;

public class Table {

    private String name;

    private Boolean needAnnotation;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}