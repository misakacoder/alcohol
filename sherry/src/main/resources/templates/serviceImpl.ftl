<#assign MYBATIS = "MYBATIS">
<#assign MYBATIS_PLUS = "MYBATIS_PLUS">
package ${packageName}.${serviceImplPackageName};

import ${packageName}.${servicePackageName}.${entity}Service;
import org.springframework.stereotype.Service;
<#if orm == MYBATIS>
import ${packageName}.${entityPackageName}.${entity};
import ${packageName}.${basePackageName}.BaseServiceImpl;
</#if>
<#if orm == MYBATIS_PLUS>
import ${packageName}.${entityPackageName}.${entity};
import ${packageName}.${mapperPackageName}.${entity}Mapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
</#if>

/**
 * <p>
 * ${table.comment} 服务实现类
 * </p>
 *
 * @author ${author}
 * @date ${date}
 */
@Service
public class ${entity}ServiceImpl<#if orm == MYBATIS> extends BaseServiceImpl<${entity}></#if><#if orm == MYBATIS_PLUS> extends ServiceImpl<${entity}Mapper, ${entity}></#if> implements ${entity}Service {

}