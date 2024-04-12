package com.scotch.kir;

import com.kir.http.HttpRequestBuilder;
import com.kir.http.Interceptor;
import com.misaka.annotation.InterceptorScope;
import com.scotch.core.ScotchContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Integer.MIN_VALUE)
@InterceptorScope(scope = InterceptorScope.Scope.ALL)
public class ScotchInterceptor implements Interceptor {

    @Autowired
    private ScotchContext scotchContext;

    @Override
    public void intercept(HttpRequestBuilder builder) {
        String url = scotchContext.getUrl(builder.url());
        builder.url(url);
    }
}
