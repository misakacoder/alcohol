<#assign MYBATIS = "MYBATIS">
<#assign MYBATIS_PLUS = "MYBATIS_PLUS">
package ${packageName}.${servicePackageName};

<#if orm == MYBATIS>
import ${packageName}.${entityPackageName}.${entity};
import ${packageName}.${basePackageName}.BaseService;
</#if>
<#if orm == MYBATIS_PLUS>
import ${packageName}.${entityPackageName}.${entity};
import com.baomidou.mybatisplus.extension.service.IService;
</#if>

/**
 * <p>
 * ${table.comment} 服务类
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
public interface ${entity}Service<#if orm == MYBATIS> extends BaseService<${entity}></#if><#if orm == MYBATIS_PLUS> extends IService<${entity}></#if> {

}