<#assign MYSQL = "MYSQL">
<#assign MYBATIS_PLUS = "MYBATIS_PLUS">
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${packageName}.${mapperPackageName}.${entity}Mapper">
    <resultMap id="BaseResultMap" type="${packageName}.${entityPackageName}.${entity}">
<#list columns as column>
   <#if column.key>
        <id column="${column.name}" jdbcType="${column.jdbcType}" property="${column.fieldName}" />
   <#else>
        <result column="${column.name}" jdbcType="${column.jdbcType}" property="${column.fieldName}" />
   </#if>
</#list>
    </resultMap>

    <sql id="Base_Column_List">
        <#list columns as column>${column.name}<#if column_has_next>, </#if></#list>
    </sql>

<#if orm != MYBATIS_PLUS>
    <insert id="insert" keyColumn="id" keyProperty="id" useGeneratedKeys="true">
        insert into ${table.name}
        (
        <include refid="Base_Column_List" />
        )
        values
        (
        <#list columns as column>
        <#noparse>#</#noparse>{${column.fieldName}, jdbcType=${column.jdbcType}}<#if column_has_next>, </#if>
        </#list>
        )
    </insert>

    <insert id="insertSelective" keyColumn="id" keyProperty="id" useGeneratedKeys="true">
        insert into ${table.name}
        <trim prefix="(" suffix=")" suffixOverrides=",">
        <#list columns as column>
            <if test="${column.fieldName} != null">
                ${column.name},
            </if>
        </#list>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
        <#list columns as column>
            <if test="${column.fieldName} != null">
                <#noparse>#</#noparse>{${column.fieldName}, jdbcType=${column.jdbcType}},
            </if>
        </#list>
        </trim>
    </insert>

    <insert id="insertList" keyColumn="id" keyProperty="id" useGeneratedKeys="true">
        <#if database == MYSQL>
        insert into ${table.name}
        (
        <include refid="Base_Column_List" />
        )
        values
        <foreach collection="entityList" item="entity" separator=",">
        (
        <#list columns as column>
        <#noparse>#</#noparse>{entity.${column.fieldName}, jdbcType=${column.jdbcType}}<#if column_has_next>, </#if>
        </#list>
        )
        </foreach>
        </#if>
    </insert>

    <delete id="deleteByPrimaryKey">
        delete from ${table.name}
        where
        <#list columns as column><#if column.key>${column.name} = <#noparse>#</#noparse>{primaryKey, jdbcType=${column.jdbcType}}</#if></#list>
    </delete>

    <delete id="delete">
        delete from ${table.name}
        where
        <trim prefixOverrides="and">
        <#list columns as column>
            <if test="${column.fieldName} != null">
                and ${column.name} = <#noparse>#</#noparse>{${column.fieldName}, jdbcType=${column.jdbcType}}
            </if>
        </#list>
        </trim>
    </delete>

    <delete id="updateByPrimaryKey">
        update ${table.name}
        <set>
        <#list columns as column>
            <#if !column.key>
            ${column.name} = <#noparse>#</#noparse>{${column.fieldName}, jdbcType=${column.jdbcType}},
            </#if>
        </#list>
        </set>
        where
        <#list columns as column><#if column.key>${column.name} = <#noparse>#</#noparse>{${column.fieldName}, jdbcType=${column.jdbcType}}</#if></#list>
    </delete>

    <delete id="updateByPrimaryKeySelective">
        update ${table.name}
        <set>
        <#list columns as column>
            <#if !column.key>
            <if test="${column.fieldName} != null">
                ${column.name} = <#noparse>#</#noparse>{${column.fieldName}, jdbcType=${column.jdbcType}},
            </if>
            </#if>
        </#list>
        </set>
        where
        <#list columns as column><#if column.key>${column.name} = <#noparse>#</#noparse>{${column.fieldName}, jdbcType=${column.jdbcType}}</#if></#list>
    </delete>

    <select id="selectByPrimaryKey" resultMap="BaseResultMap">
        select
        <include refid="Base_Column_List" />
        from ${table.name}
        where
        <#list columns as column><#if column.key>${column.name} = <#noparse>#</#noparse>{primaryKey, jdbcType=${column.jdbcType}}</#if></#list>
    </select>

    <select id="selectOne" resultMap="BaseResultMap">
        <include refid="select" />
    </select>

    <select id="select" resultMap="BaseResultMap">
        <include refid="select" />
    </select>

    <select id="list" resultMap="BaseResultMap">
        <include refid="select" />
    </select>

    <select id="selectAll" resultMap="BaseResultMap">
        <include refid="select" />
    </select>

    <select id="count" resultType="java.lang.Long">
        select count(1) from ${table.name}
    </select>

    <select id="page" resultMap="BaseResultMap">
        <include refid="select" />
    </select>

    <sql id="select">
        select
        <include refid="Base_Column_List" />
        from ${table.name}
        <include refid="selectByBean" />
    </sql>

    <sql id="selectByBean">
        <where>
        </where>
    </sql>
</#if>
</mapper>