package com.kir.http;

import com.rye.util.StringUtil;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.*;

public class HttpRequestBuilder {

    private static final String CONTENT_TYPE = "Content-Type";

    private String url;

    private RequestMethod method;

    private Duration timeout = Duration.ofSeconds(3L);

    private final Map<String, List<String>> header = new HashMap<>();

    private Map<String, Object> form;

    private String body;

    public HttpRequestBuilder url(String url) {
        this.url = url;
        return this;
    }

    public HttpRequestBuilder method(RequestMethod method) {
        this.method = method;
        return this;
    }

    public HttpRequestBuilder timeout(long timeout) {
        this.timeout = Duration.ofSeconds(timeout);
        return this;
    }

    public HttpRequestBuilder form(Map<String, Object> form) {
        this.form = form;
        this.body = null;
        return this;
    }

    public HttpRequestBuilder header(String name, String value) {
        if (StringUtil.isAllNotBlank(name, value)) {
            this.header.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
        }
        return this;
    }

    public HttpRequestBuilder header(Map<String, List<String>> header) {
        if (header != null) {
            header.forEach((k, v) -> this.header.computeIfAbsent(k, key -> new ArrayList<>()).addAll(v));
        }
        return this;
    }

    public HttpRequestBuilder body(String body) {
        this.body = body;
        this.form = null;
        return this;
    }

    public HttpRequest build() {
        if (url == null) {
            throw new IllegalStateException("Url is null");
        }
        if (method == null) {
            throw new IllegalStateException("Method is null");
        }
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(this.url))
                .timeout(timeout);
        if (!header.isEmpty()) {
            header.forEach((k, v) -> v.forEach(p -> builder.header(k, p)));
        }
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();
        if (form != null) {
            builder.header(CONTENT_TYPE, MultipartBody.getContentType());
            publisher = MultipartBody.ofStreamMultipartBody(form);
        }
        if (body != null) {
            builder.header(CONTENT_TYPE, "application/json");
            publisher = HttpRequest.BodyPublishers.ofString(body);
        }
        builder.method(this.method.name(), publisher);
        return builder.build();
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("Request Url: " + url);
        joiner.add("Request Method: " + method.name());
        joiner.add("Request Headers: ");
        header.forEach((k, v) -> joiner.add(String.format("    %s: %s", k, String.join(",", v))));
        joiner.add("Request Form: ");
        if (form != null) {
            form.forEach((k, v) -> joiner.add(String.format("    %s: %s", k, v)));
        }
        joiner.add("Request Body: ");
        joiner.add("    " + body);
        return joiner.toString();
    }
}
