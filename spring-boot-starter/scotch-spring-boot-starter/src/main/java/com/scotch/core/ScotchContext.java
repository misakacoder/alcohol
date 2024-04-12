package com.scotch.core;

import com.misaka.annotation.Kir;
import com.rye.util.ClassUtil;
import com.scotch.data.Producer;
import com.scotch.kir.ScotchServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ScotchContext implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ScotchContext.class);

    @Autowired
    private Environment environment;

    @Autowired
    private ScotchServer scotchServer;

    public final Map<String, List<Producer>> producers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(2);

    @Override
    public void afterPropertiesSet() throws Exception {
        Boolean sslEnabled = environment.getProperty("server.ssl.enabled", Boolean.class, false);
        String keyStoreType = environment.getProperty("server.ssl.key-store-type", String.class, "");
        Producer producer = new Producer();
        producer.setScheme(sslEnabled && !keyStoreType.isEmpty() ? "https" : "http");
        producer.setPort(environment.getProperty("server.port", Integer.class));
        producer.setAppName(environment.getProperty("spring.application.name"));
        try {
            scotchServer.register(producer);
        } catch (Exception e) {
            log.error("Failed to register scotch server", e);
            return;
        }

        executorService.scheduleWithFixedDelay(this::pull, 0L, 1L, TimeUnit.MINUTES);
        executorService.scheduleWithFixedDelay(this::heartbeat, 0L, 30L, TimeUnit.SECONDS);
    }

    public String getUrl(String appName) {
        String url = appName;
        List<Producer> producers = this.producers.get(appName);
        if (!CollectionUtils.isEmpty(producers)) {
            int i = new Random(System.currentTimeMillis()).nextInt(producers.size());
            Producer producer = producers.get(i);
            url = String.format("%s://%s:%s", producer.getScheme(), producer.getHost(), producer.getPort());
        }
        return url;
    }

    private void pull() {
        try {
            Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation("", Kir.class);
            Set<String> appNames = new LinkedHashSet<>();
            for (Class<?> cls : classes) {
                Kir kir = cls.getAnnotation(Kir.class);
                String value = kir.url();
                value = environment.resolvePlaceholders(value);
                if (!isUrl(value)) {
                    appNames.add(value);
                }
            }
            producers.clear();
            Map<String, Set<Producer>> producers = scotchServer.pull(appNames);
            producers.forEach((k, v) -> {
                if (!CollectionUtils.isEmpty(v)) {
                    this.producers.put(k, new ArrayList<>(v));
                }
            });
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void heartbeat() {
        try {
            Producer producer = new Producer();
            producer.setPort(environment.getProperty("server.port", Integer.class));
            producer.setAppName(environment.getProperty("spring.application.name"));
            scotchServer.heartbeat(producer);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private boolean isUrl(String value) {
        return value != null && (value.startsWith("http://") || value.startsWith("https://"));
    }
}
