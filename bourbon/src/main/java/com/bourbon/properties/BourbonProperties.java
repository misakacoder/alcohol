package com.bourbon.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static com.bourbon.properties.BourbonProperties.PREFIX;

@Configuration
@ConfigurationProperties(prefix = PREFIX)
public class BourbonProperties {

    public static final String PREFIX = "bourbon";

    private BasicAuth basic;

    private String searchLocation;

    public BasicAuth getBasic() {
        return basic;
    }

    public void setBasic(BasicAuth basic) {
        this.basic = basic;
    }

    public String getSearchLocation() {
        return searchLocation;
    }

    public void setSearchLocation(String searchLocation) {
        this.searchLocation = searchLocation;
    }
}