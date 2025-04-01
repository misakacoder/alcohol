package com.vermouth.spi;

import com.alipay.remoting.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServiceLoader {

    private static final String PREFIX = "META-INF/services/";

    private static final Map<Class<?>, List<Class<?>>> SERVICES = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> List<Class<T>> load(Class<T> service) {
        List<Class<?>> services = SERVICES.getOrDefault(service, new ArrayList<>());
        if (services.isEmpty()) {
            ClassLoader classLoader = ServiceLoader.class.getClassLoader();
            String name = PREFIX + service.getName();
            try {
                Enumeration<URL> urls = classLoader.getResources(name);
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    try (InputStream in = url.openStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            final String className = line.trim();
                            if (StringUtils.isNotBlank(className)
                                    && !className.startsWith("#")
                                    && services.stream().noneMatch(p -> p.getName().equals(className))) {
                                try {
                                    Class<?> cls = Class.forName(className, false, classLoader);
                                    if (cls != service && service.isAssignableFrom(cls)) {
                                        services.add(cls);
                                    } else {
                                        error(service, String.format("Provider %s not a subclass", className));
                                    }
                                } catch (ClassNotFoundException e) {
                                    error(service, String.format("Provider %s not found", className));
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                error(service, "Error locating configuration files");
            }
            SERVICES.put(service, services);
        }
        return services.stream().map(p -> (Class<T>) p).collect(Collectors.toList());
    }

    public static <T> List<T> load(Class<T> service, Function<Class<T>, T> init) {
        if (init == null) {
            init = cls -> {
                try {
                    return cls.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Error creating instance from %s", service.getName()), e);
                }
            };
        }
        List<Class<T>> classList = load(service);
        List<T> serviceList = new ArrayList<>();
        for (Class<T> cls : classList) {
            serviceList.add(init.apply(cls));
        }
        return serviceList;
    }

    private static void error(Class<?> service, String message) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(String.format("%s: %s", service, message));
    }
}