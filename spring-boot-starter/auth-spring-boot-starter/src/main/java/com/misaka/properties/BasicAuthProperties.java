package com.misaka.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.misaka.properties.BasicAuthProperties.PREFIX;

@ConfigurationProperties(prefix = PREFIX)
public class BasicAuthProperties {

    public static final String PREFIX = "auth.basic";

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
