package com.kir.http;

public interface Interceptor {
    void intercept(HttpRequestBuilder builder);
}
