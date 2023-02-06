<#assign TK_MYBATIS = "TK_MYBATIS">
package ${packageName}.${configPackageName};

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
<#if orm == TK_MYBATIS>
import tk.mybatis.spring.annotation.MapperScan;
<#else>
import org.mybatis.spring.annotation.MapperScan;
</#if>
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ${packageName}.${interceptorPackageName}.MyBatisQueryInterceptor;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "${packageName}.${mapperPackageName}")
public class MybatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:**/mapper/*Mapper.xml"));
        Interceptor[] interceptors = {pageInterceptor(), new MyBatisQueryInterceptor()};
        sqlSessionFactoryBean.setPlugins(interceptors);
        return sqlSessionFactoryBean.getObject();
    }

    private PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        //pageNum < 0 ? pageNum = 1 and pageNum > lastPage ? pageNum = lastPage
        properties.setProperty("reasonable", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }
}
