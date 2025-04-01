package com.bourbon.vermouth.entity;

import java.io.Serializable;

public class ConfigPublisher implements Serializable {

    private String filename;

    private Boolean appName;

    private String profile;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Boolean getAppName() {
        return appName;
    }

    public void setAppName(Boolean appName) {
        this.appName = appName;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
