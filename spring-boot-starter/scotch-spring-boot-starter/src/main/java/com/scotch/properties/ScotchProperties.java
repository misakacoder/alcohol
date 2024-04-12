package com.scotch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = ScotchProperties.PREFIX)
public class ScotchProperties {

    public static final String PREFIX = "scotch";

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

    public static class BasicAuth {

        private boolean enable = false;

        private String username;

        private String password;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

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
}