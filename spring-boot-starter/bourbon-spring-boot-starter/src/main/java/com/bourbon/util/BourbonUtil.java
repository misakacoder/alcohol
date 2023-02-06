package com.bourbon.util;

import com.bourbon.properties.BasicAuth;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BourbonUtil {

    public static String urlJoiner(String url, String appName, String profile) {
        if (StringUtils.hasText(url) && StringUtils.hasText(appName)) {
            url = String.format("%s/bourbon/config/%s", url, appName);
            if (StringUtils.hasText(profile)) {
                url = String.format("%s?profile=%s", url, profile);
            }
            return url;
        }
        return null;
    }

    public static String[] getAuthorizationHeader(BasicAuth basicAuth) {
        List<String> headers = new ArrayList<>();
        if (basicAuth != null && basicAuth.isEnable()) {
            String username = basicAuth.getUsername();
            String password = basicAuth.getPassword();
            byte[] bytes = String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8);
            String authorization = "Basic " + Base64Utils.encodeToString(bytes);
            headers.add("Authorization");
            headers.add(authorization);
        }
        return headers.toArray(new String[0]);
    }
}
