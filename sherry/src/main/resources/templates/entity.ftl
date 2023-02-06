<#assign MYBATIS_PLUS = "MYBATIS_PLUS">
<#assign TK_MYBATIS = "TK_MYBATIS">
package ${packageName}.${entityPackageName};

import java.io.Serializable;
<#list enums as enum>
import ${packageName}.${enumPackageName}.${enum};
</#list>

/**
 * <p>
 * ${table.comment}
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
<#if table.needAnnotation>
    <#if orm = MYBATIS_PLUS>
@TableName("${table.name}")
    </#if>
    <#if orm = TK_MYBATIS>
@TableName("${table.name}")
    </#if>
</#if>
public class ${entity} implements Serializable {

    private static final long serialVersionUID = 1L;
<#list columns as column>

    <#if column.key>
        <#if orm = MYBATIS_PLUS>
    @TableId(value = "${column.name}"<#if column.incr>, type = IdType.AUTO</#if>)
        </#if>
        <#if orm = TK_MYBATIS>
    @Id
            <#if column.needAnnotation>
    @Column(name = "${column.name}")
            </#if>
        </#if>
    <#else>
        <#if column.needAnnotation>
            <#if orm == MYBATIS_PLUS>
    @TableField("${column.name}")
            </#if>
            <#if orm = TK_MYBATIS>
    @Column(name = "${column.name}")
            </#if>
        </#if>
    </#if>
    <#if column.javaType == "Date">
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    </#if>
    private ${column.javaType} ${column.fieldName};
</#list>
<#list columns as column>

    <#if column.javaType == "boolean">
        <#assign getPrefix = "is">
    <#else>
        <#assign getPrefix = "get">
    </#if>
    public ${column.javaType} ${getPrefix}${column.upperFieldName}() {
        return ${column.fieldName};
    }

    public void set${column.upperFieldName}(${column.javaType} ${column.fieldName}) {
        this.${column.fieldName} = ${column.fieldName};
    }
</#list>
}