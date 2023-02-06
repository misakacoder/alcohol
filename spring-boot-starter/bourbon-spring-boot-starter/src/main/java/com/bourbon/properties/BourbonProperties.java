package com.bourbon.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.bourbon.properties.BourbonProperties.PREFIX;


@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class BourbonProperties {

    public static final String PREFIX = "bourbon";

    private boolean enable = false;

    private String url;

    private BasicAuth basic;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BasicAuth getBasic() {
        return basic;
    }

    public void setBasic(BasicAuth basic) {
        this.basic = basic;
    }
}