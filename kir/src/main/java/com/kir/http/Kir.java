package com.kir.http;

import com.kir.annotation.*;
import com.kir.decoder.Decoder;
import com.kir.decoder.GsonDecoder;
import com.kir.encoder.Encoder;
import com.kir.encoder.GsonEncoder;
import com.rye.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class Kir implements InvocationHandler {

    private static final String PLACEHOLDER = "{%s}";
    private static final Logger log = LoggerFactory.getLogger(Kir.class);

    private final Builder builder;
    private final HttpClient httpClient;

    private Kir(Builder builder) {
        this.builder = builder;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(builder.connectTimeout)).build();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            throw new UnsupportedOperationException();
        }
        RequestMetadata metadata = getMetadata(method);
        if (metadata == null) {
            return null;
        }
        StringBuilder pathBuilder = new StringBuilder();
        long timeout = metadata.getTimeout();
        Map<String, List<String>> header = getHeader(method);
        Map<String, Object> form = new HashMap<>();
        StringBuilder bodyBuilder = new StringBuilder();
        setBody(method, args, metadata, pathBuilder, header, form, bodyBuilder);
        String path = StringUtil.trim(pathBuilder.toString(), "/");
        String body = bodyBuilder.toString();
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder()
                .method(metadata.getMethod())
                .timeout(timeout > 0L ? timeout : builder.readTimeout)
                .header(header);
        if (!form.isEmpty() && StringUtil.isNotBlank(body)) {
            String params = form.entrySet().stream()
                    .map(p -> p.getKey() + "=" + p.getValue().toString())
                    .collect(Collectors.joining("&"));
            path = path + "?" + params;
            requestBuilder.body(body);
        } else if (!form.isEmpty()) {
            requestBuilder.form(form);
        } else if (StringUtil.isNotBlank(body)) {
            requestBuilder.body(body);
        }
        requestBuilder.url(StringUtil.trim(builder.url, "/") + "/" + path);
        log.debug("\n{}", requestBuilder);
        for (Interceptor interceptor : builder.interceptors) {
            if (interceptor != null) {
                interceptor.intercept(requestBuilder);
            }
        }
        HttpRequest httpRequest = requestBuilder.build();
        DownloadFile saveFile = method.getAnnotation(DownloadFile.class);
        Class<?> returnType = method.getReturnType();
        if (saveFile != null) {
            if (returnType != String.class) {
                throw new RuntimeException("The return value of the method to download a file must be a string type");
            }
            HttpResponse<Path> response = httpClient.send(httpRequest, getFileBodyHandler(saveFile));
            String filepath = response.body().normalize().toFile().getAbsolutePath();
            if (response.statusCode() != 200) {
                File errorFile = new File(filepath.substring(0, filepath.lastIndexOf(saveFile.ext())) + "error.txt");
                boolean isRenamed = new File(filepath).renameTo(errorFile);
                if (isRenamed) {
                    filepath = errorFile.getAbsolutePath();
                }
            }
            return filepath;
        } else {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException(String.format("Request failed, the response status code is %s, and the response body is %s", response.statusCode(), response.body()));
            }
            return Decoder.decode(response.body(), method, builder.decoder);
        }
    }

    private RequestMetadata getMetadata(Method method) {
        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
        if (requestMapping != null) {
            return new RequestMetadata(requestMapping.value(), requestMapping.method(), requestMapping.timeout());
        }
        List<Annotation> mappingList = new ArrayList<>();
        mappingList.add(method.getAnnotation(GetMapping.class));
        mappingList.add(method.getAnnotation(PostMapping.class));
        mappingList.add(method.getAnnotation(PutMapping.class));
        mappingList.add(method.getAnnotation(DeleteMapping.class));
        for (Annotation mapping : mappingList) {
            if (mapping != null) {
                try {
                    Class<? extends Annotation> annotationClass = mapping.annotationType();
                    Method value = annotationClass.getDeclaredMethod("value");
                    Method timeout = annotationClass.getDeclaredMethod("timeout");
                    RequestMethod requestMethod = annotationClass.getAnnotation(RequestMapping.class).method();
                    return new RequestMetadata((String) value.invoke(mapping), requestMethod, (Long) timeout.invoke(mapping));
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
        return null;
    }

    private Map<String, List<String>> getHeader(Method method) {
        Class<?> methodDeclaringClass = method.getDeclaringClass();
        Map<String, List<String>> headerMap = new HashMap<>();
        List<HttpHeader> headerList = new ArrayList<>();
        HttpHeaders httpHeaders = methodDeclaringClass.getAnnotation(HttpHeaders.class);
        if (httpHeaders != null) {
            headerList.addAll(Arrays.asList(httpHeaders.value()));
        }
        httpHeaders = method.getAnnotation(HttpHeaders.class);
        if (httpHeaders != null) {
            headerList.addAll(Arrays.asList(httpHeaders.value()));
        }
        headerList.add(methodDeclaringClass.getAnnotation(HttpHeader.class));
        headerList.add(method.getAnnotation(HttpHeader.class));
        for (HttpHeader httpHeader : headerList) {
            if (httpHeader != null) {
                String name = httpHeader.name();
                String value = httpHeader.value();
                if (StringUtil.isAllNotBlank(name, value)) {
                    headerMap.computeIfAbsent(name, key -> new ArrayList<>()).add(httpHeader.value());
                }
            }
        }
        return headerMap;
    }

    private void setBody(Method method, Object[] args, RequestMetadata metadata, StringBuilder pathBuilder, Map<String, List<String>> header, Map<String, Object> form, StringBuilder bodyBuilder) throws Exception {
        Parameter[] parameters = method.getParameters();
        String path = metadata.getPath();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = args[i];
            if (value != null) {
                PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
                RequestHeader requestHeader = parameter.getAnnotation(RequestHeader.class);
                if (pathVariable != null && StringUtil.isNotBlank(pathVariable.value())) {
                    String placeholder = String.format(PLACEHOLDER, pathVariable.value());
                    path = path.replace(placeholder, StringUtil.toString(value));
                } else if (requestHeader != null) {
                    String name = requestHeader.value();
                    String valueString = value.toString();
                    if (StringUtil.isAllNotBlank(name, valueString)) {
                        header.computeIfAbsent(name, key -> new ArrayList<>()).add(valueString);
                    }
                } else {
                    RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                    if (requestBody != null) {
                        bodyBuilder.setLength(0);
                        bodyBuilder.append(builder.encoder.encode(value));
                    }
                    if (requestParam != null) {
                        String name = requestParam.value();
                        if (value instanceof List) {
                            List<?> data = (List<?>) value;
                            String valueString = data.stream()
                                    .filter(Objects::nonNull)
                                    .map(Object::toString)
                                    .collect(Collectors.joining(","));
                            form.put(name, valueString);
                        } else if (value instanceof File) {
                            if (metadata.getMethod() == RequestMethod.GET) {
                                throw new RuntimeException("Http method GET do not support upload file");
                            }
                            if (bodyBuilder.length() > 0) {
                                throw new RuntimeException("Request body is not empty, you cannot upload file");
                            }
                            form.put(name, value);
                        } else {
                            form.put(name, value.toString());
                        }
                    } else {
                        if (value instanceof Map) {
                            Map<?, ?> data = (Map<?, ?>) value;
                            data.forEach((k, v) -> {
                                if (k != null && v != null) {
                                    form.put(k.toString(), v.toString());
                                }
                            });
                        } else {
                            Field[] fields = value.getClass().getDeclaredFields();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                Object fieldValue = field.get(value);
                                if (fieldValue != null) {
                                    form.put(field.getName(), fieldValue.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        pathBuilder.append(path);
    }

    private HttpResponse.BodyHandler<Path> getFileBodyHandler(DownloadFile saveFile) {
        File dir = new File(saveFile.dir()).getAbsoluteFile();
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("Directory {} does not exist, download to the temp directory", saveFile.dir());
            dir = new File(System.getProperty("java.io.tmpdir"));
        }
        String filename = String.format("%s.%s", UUID.randomUUID(), saveFile.ext());
        return HttpResponse.BodyHandlers.ofFile(new File(dir, filename).toPath());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String url;

        private long readTimeout = 10L;

        private long connectTimeout = 5L;

        private Encoder encoder = new GsonEncoder();

        private Decoder decoder = new GsonDecoder();

        private final List<Interceptor> interceptors = new ArrayList<>();

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder readTimeout(long readTimeout) {
            if (readTimeout > 0L) {
                this.readTimeout = readTimeout;
            }
            return this;
        }

        public Builder connectTimeout(long connectTimeout) {
            if (connectTimeout > 0L) {
                this.connectTimeout = connectTimeout;
            }
            return this;
        }

        public Builder encoder(Encoder encoder) {
            if (encoder != null) {
                this.encoder = encoder;
            }
            return this;
        }

        public Builder decoder(Decoder decoder) {
            if (decoder != null) {
                this.decoder = decoder;
            }
            return this;
        }

        public Builder interceptor(Interceptor interceptor) {
            if (interceptor != null) {
                this.interceptors.add(interceptor);
            }
            return this;
        }

        public Builder interceptors(List<Interceptor> interceptors) {
            if (interceptors != null) {
                this.interceptors.addAll(interceptors);
            }
            return this;
        }

        public <T> T target(Class<T> apiType) {
            return (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{apiType}, new Kir(this));
        }
    }
}
