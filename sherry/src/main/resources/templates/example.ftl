package ${packageName}.${entityPackageName};

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
<#list enums as enum>
    <#if enum_index == 0>
import java.util.stream.Collectors;
    </#if>
import ${packageName}.${enumPackageName}.${enum};
</#list>

public class ${entity}Example {

    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public ${entity}Example() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {

        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }
<#list columns as column>

        public Criteria and${column.upperFieldName}IsNull() {
            addCriterion("${column.name} is null");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}IsNotNull() {
            addCriterion("${column.name} is not null");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}EqualTo(${column.javaType} value) {
            addCriterion("${column.name} =", <#if column.enumType>value.getValue()<#else>value</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}NotEqualTo(${column.javaType} value) {
            addCriterion("${column.name} <>", <#if column.enumType>value.getValue()<#else>value</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}GreaterThan(${column.javaType} value) {
            addCriterion("${column.name} >", <#if column.enumType>value.getValue()<#else>value</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}GreaterThanOrEqualTo(${column.javaType} value) {
            addCriterion("${column.name} >=", <#if column.enumType>value.getValue()<#else>value</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}LessThan(${column.javaType} value) {
            addCriterion("${column.name} <", <#if column.enumType>value.getValue()<#else>value</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}LessThanOrEqualTo(${column.javaType} value) {
            addCriterion("${column.name} <=", <#if column.enumType>value.getValue()<#else>value</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}In(List<${column.javaType}> values) {
            addCriterion("${column.name} in", <#if column.enumType>values.stream().map(${column.javaType}::getValue).collect(Collectors.toList())<#else>values</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}NotIn(List<${column.javaType}> values) {
            addCriterion("${column.name} not in", <#if column.enumType>values.stream().map(${column.javaType}::getValue).collect(Collectors.toList())<#else>values</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}Between(${column.javaType} value1, ${column.javaType} value2) {
            addCriterion("${column.name} between", <#if column.enumType>value1.getValue()<#else>value1</#if>, <#if column.enumType>value2.getValue()<#else>value2</#if>, "${column.name}");
            return (Criteria) this;
        }

        public Criteria and${column.upperFieldName}NotBetween(${column.javaType} value1, ${column.javaType} value2) {
            addCriterion("${column.name} not between", <#if column.enumType>value1.getValue()<#else>value1</#if>, <#if column.enumType>value2.getValue()<#else>value2</#if>, "${column.name}");
            return (Criteria) this;
        }
</#list>
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {

        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}