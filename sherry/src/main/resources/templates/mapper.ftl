<#assign MYBATIS = "MYBATIS">
<#assign MYBATIS_PLUS = "MYBATIS_PLUS">
<#assign TK_MYBATIS = "TK_MYBATIS">
package ${packageName}.${mapperPackageName};

import ${packageName}.${entityPackageName}.${entity};
<#if orm == MYBATIS>
import ${packageName}.${basePackageName}.BaseMapper;
</#if>
<#if orm == MYBATIS_PLUS>
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
</#if>
<#if orm == TK_MYBATIS>
import tk.mybatis.mapper.common.Mapper;
</#if>

/**
 * <p>
 * ${table.comment} Mapper接口
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
public interface ${entity}Mapper<#if orm == TK_MYBATIS> extends Mapper<${entity}><#else> extends BaseMapper<${entity}></#if> {

}