package com.kir.http;

public class RequestMetadata {

    private final String path;

    private final RequestMethod method;

    private final long timeout;

    public RequestMetadata(String path, RequestMethod method, long timeout) {
        this.path = path;
        this.method = method;
        this.timeout = timeout;
    }

    public String getPath() {
        return path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public long getTimeout() {
        return timeout;
    }
}
