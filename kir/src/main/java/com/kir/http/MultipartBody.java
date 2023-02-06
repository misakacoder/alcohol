package com.kir.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpRequest;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class MultipartBody {

    private static final String BOUNDARY = "Misaka_" + UUID.randomUUID().toString().replace("-", "");
    private static final String BOUNDARY_START = String.format("--%s\r\n", BOUNDARY);
    private static final String BOUNDARY_END = String.format("--%s--\r\n", BOUNDARY);
    private static final String CONTENT_DISPOSITION_TEMPLATE = "Content-Disposition: form-data; name=\"%s\"\r\n\r\n";
    private static final String CONTENT_DISPOSITION_FILE_TEMPLATE = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\n";
    private static final String CONTENT_TYPE_FILE_TEMPLATE = "Content-Type: %s\r\n\r\n";
    private static final Logger log = LoggerFactory.getLogger(MultipartBody.class);

    public static String getContentType() {
        return "multipart/form-data; boundary=" + BOUNDARY;
    }

    public static HttpRequest.BodyPublisher ofMultipartBody(Map<String, Object> form) {
        if (form != null && !form.isEmpty()) {
            List<byte[]> bytes = new ArrayList<>();
            Set<Map.Entry<String, Object>> entries = form.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                bytes.add(BOUNDARY_START.getBytes(StandardCharsets.UTF_8));
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof File) {
                    File file = (File) value;
                    if (!file.exists() || file.isDirectory()) {
                        continue;
                    }
                    bytes.add(String.format(CONTENT_DISPOSITION_FILE_TEMPLATE, name, file.getName()).getBytes(StandardCharsets.UTF_8));
                    bytes.add(String.format(CONTENT_TYPE_FILE_TEMPLATE, getMimeType(file)).getBytes(StandardCharsets.UTF_8));
                    bytes.add(readAllBytes(file));
                } else {
                    bytes.add(String.format(CONTENT_DISPOSITION_TEMPLATE, name).getBytes());
                    bytes.add(value.toString().getBytes(StandardCharsets.UTF_8));
                }
                bytes.add("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            bytes.add(BOUNDARY_END.getBytes(StandardCharsets.UTF_8));
            return HttpRequest.BodyPublishers.ofByteArrays(bytes);
        }
        return HttpRequest.BodyPublishers.noBody();
    }

    public static HttpRequest.BodyPublisher ofStreamMultipartBody(Map<String, Object> form) {
        if (form != null && !form.isEmpty()) {
            try {
                Pipe pipe = Pipe.open();
                new Thread(() -> {
                    try (OutputStream os = Channels.newOutputStream(pipe.sink())) {
                        Set<Map.Entry<String, Object>> entries = form.entrySet();
                        for (Map.Entry<String, Object> entry : entries) {
                            os.write(BOUNDARY_START.getBytes(StandardCharsets.UTF_8));
                            String name = entry.getKey();
                            Object value = entry.getValue();
                            if (value != null) {
                                if (value instanceof File) {
                                    File file = (File) value;
                                    if (!file.exists() || file.isDirectory()) {
                                        continue;
                                    }
                                    os.write(String.format(CONTENT_DISPOSITION_FILE_TEMPLATE, name, file.getName()).getBytes(StandardCharsets.UTF_8));
                                    os.write(String.format(CONTENT_TYPE_FILE_TEMPLATE, getMimeType(file)).getBytes(StandardCharsets.UTF_8));
                                    try (InputStream is = new FileInputStream(file)) {
                                        int len = 0;
                                        byte[] buffer = new byte[1024 * 1024];
                                        while ((len = is.read(buffer)) != -1) {
                                            os.write(buffer, 0, len);
                                        }
                                    } catch (Exception e) {
                                        log.error("", e);
                                    }
                                } else {
                                    String valueString = null;
                                    if (value instanceof List) {
                                        List<?> data = (List<?>) value;
                                        valueString = data.stream()
                                                .filter(Objects::nonNull)
                                                .map(Object::toString)
                                                .collect(Collectors.joining(","));
                                    } else {
                                        valueString = value.toString();
                                    }
                                    os.write(String.format(CONTENT_DISPOSITION_TEMPLATE, name).getBytes());
                                    os.write(valueString.getBytes(StandardCharsets.UTF_8));
                                }
                                os.write("\r\n".getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        os.write(BOUNDARY_END.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }).start();
                return HttpRequest.BodyPublishers.ofInputStream(() -> Channels.newInputStream(pipe.source()));
            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException(e);
            }
        }
        return HttpRequest.BodyPublishers.noBody();
    }

    private static String getMimeType(File file) {
        try {
            return Files.probeContentType(file.toPath());
        } catch (Exception e) {
            log.error("", e);
        }
        return "application/octet-stream";
    }

    private static byte[] readAllBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }
}
