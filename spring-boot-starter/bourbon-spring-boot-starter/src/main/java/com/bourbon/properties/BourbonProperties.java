package com.bourbon.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.bourbon.properties.BourbonProperties.PREFIX;


@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class BourbonProperties {

    public static final String PREFIX = "bourbon";

    private boolean enabled = false;

    private String url;

    private BasicAuth basic;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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