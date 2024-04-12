package com.scotch.kir;

import com.kir.http.HttpRequestBuilder;
import com.kir.http.Interceptor;
import com.scotch.properties.ScotchProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

@Component
public class ScotchAuthInterceptor implements Interceptor {

    @Autowired
    private ScotchProperties scotchProperties;

    @Override
    public void intercept(HttpRequestBuilder builder) {
        ScotchProperties.BasicAuth basicAuth = scotchProperties.getBasic();
        if (basicAuth.isEnable()) {
            byte[] bytes = String.format("%s:%s", basicAuth.getUsername(), basicAuth.getPassword()).getBytes(StandardCharsets.UTF_8);
            String authorization = "Basic " + Base64Utils.encodeToString(bytes);
            builder.header("Authorization", authorization);
        }
    }
}
