package com.bourbon.loader;

import com.bourbon.properties.BourbonProperties;
import org.springframework.boot.context.config.ConfigDataResource;

public class BourbonConfigDataResource extends ConfigDataResource {

    private final BourbonProperties bourbonProperties;

    private final String filename;

    private final boolean isAppName;

    private final String profile;

    public BourbonConfigDataResource(BourbonProperties bourbonProperties, String filename, boolean isAppName, String profile) {
        this.bourbonProperties = bourbonProperties;
        this.filename = filename;
        this.isAppName = isAppName;
        this.profile = profile;
    }

    public BourbonProperties getBourbonProperties() {
        return bourbonProperties;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isAppName() {
        return isAppName;
    }

    public String getProfile() {
        return profile;
    }
}
