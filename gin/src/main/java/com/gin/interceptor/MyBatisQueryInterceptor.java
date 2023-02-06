package com.gin.interceptor;

import cn.hutool.core.convert.Convert;
import com.gin.base.BaseList;
import com.gin.base.BasePage;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

/**
 * <p>
 * Mybatis查询拦截器
 * </p>
 *
 * @author sherry
 * @date 2022-10-27
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class MyBatisQueryInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        beforeProceed(invocation);
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
        Interceptor.super.setProperties(properties);
    }

    private void beforeProceed(Invocation invocation) {
        Object[] args = invocation.getArgs();
        MappedStatement mappedStatement = (MappedStatement) args[0];
        Object param = args[1];
        if (mappedStatement != null) {
            String id = mappedStatement.getId();
            String methodName = "selectOne";
            if (id.endsWith(methodName)) {
                PageHelper.startPage(1, 1, false);
                return;
            }
        }
        if (param != null) {
            if (param instanceof BaseList) {
                BaseList baseList = (BaseList) param;
                Integer limit = Convert.toInt(baseList.getLimit(), BaseList.DEFAULT_LIMIT);
                PageHelper.startPage(1, limit, false).setOrderBy(baseList.getOrderBy());
            } else if (param instanceof BasePage) {
                BasePage basePage = (BasePage) param;
                Integer pageNum = Convert.toInt(basePage.getPageNum(), BasePage.DEFAULT_PAGE_NUM);
                Integer pageSize = Convert.toInt(basePage.getPageSize(), BasePage.DEFAULT_PAGE_SIZE);
                PageHelper.startPage(pageNum, pageSize, basePage.getOrderBy());
            }
        }
    }
}
