package com.bourbon.util;

import com.bourbon.properties.BasicAuth;
import com.bourbon.properties.BourbonProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BourbonUtil {

    public static String urlJoiner(String url, String filename, Boolean isAppName, String profile) {
        if (StringUtils.hasText(url) && StringUtils.hasText(filename)) {
            url = String.format("%s/bourbon/config/%s", url, filename);
            StringJoiner joiner = new StringJoiner("&");
            if (isAppName != null) {
                joiner.add(String.format("isAppName=%s", isAppName));
            }
            if (StringUtils.hasText(profile)) {
                joiner.add(String.format("profile=%s", profile));
            }
            if (joiner.length() > 0) {
                url = String.format("%s?%s", url, joiner);
            }
            return url;
        }
        return null;
    }

    public static String[] getAuthorizationHeader(BasicAuth basicAuth) {
        List<String> headers = new ArrayList<>();
        if (basicAuth != null && basicAuth.isEnabled()) {
            String username = basicAuth.getUsername();
            String password = basicAuth.getPassword();
            byte[] bytes = String.format("%s:%s", username, password).getBytes(StandardCharsets.UTF_8);
            String authorization = "Basic " + Base64Utils.encodeToString(bytes);
            headers.add("Authorization");
            headers.add(authorization);
        }
        return headers.toArray(new String[0]);
    }

    public static MapPropertySource pullConfig(BourbonProperties bourbonProperties, String filename, Boolean isAppName, String profile) {
        if (bourbonProperties.isEnabled()) {
            String url = bourbonProperties.getUrl();
            url = BourbonUtil.urlJoiner(url, filename, isAppName, profile);
            if (StringUtils.hasText(url)) {
                try {
                    String[] headers = BourbonUtil.getAuthorizationHeader(bourbonProperties.getBasic());
                    HttpResponse<String> response = HttpUtil.get(url, Duration.ofSeconds(5L), headers);
                    if (response.statusCode() != 200) {
                        String message = String.format("Failed to load bourbon configuration, the response code is %s and the response body is %s", response.statusCode(), response.body());
                        throw new RuntimeException(message);
                    }
                    Map<String, Object> applicationMap = new ObjectMapper().readValue(response.body(), new TypeReference<>() {
                    });
                    return new MapPropertySource(filename, applicationMap);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Failed to connect to %s", url), e);
                }
            }
        }
        return null;
    }
}
