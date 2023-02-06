package com.gin.properties;

import com.bourbon.refresh.RefreshScope;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.gin.properties.ServerProperties.PREFIX;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class ServerProperties {

    public static final String PREFIX = "server";

    private String author;

    private String version;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
