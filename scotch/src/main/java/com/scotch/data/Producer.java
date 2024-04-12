package com.scotch.data;

import java.util.Objects;

public class Producer {

    private String scheme;

    private String host;

    private Integer port;

    private String appName;

    private Long lastActiveMillis = System.currentTimeMillis();

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getLastActiveMillis() {
        return lastActiveMillis;
    }

    public void setLastActiveMillis(Long lastActiveMillis) {
        this.lastActiveMillis = lastActiveMillis;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, host, port);
    }

    @Override
    public boolean equals(Object object) {
        boolean equals = false;
        if (object instanceof Producer) {
            Producer producer = (Producer) object;
            equals = Objects.equals(appName, producer.appName) && Objects.equals(host, producer.host) && Objects.equals(port, producer.port);
        }
        return equals;
    }
}
