package com.scotch.core;

import com.scotch.data.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ScotchRegistry implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(ScotchRegistry.class);

    private final Map<String, Set<Producer>> producers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    public Map<String, Set<Producer>> list() {
        return producers;
    }

    public void register(Producer producer) {
        producers.computeIfAbsent(producer.getAppName(), k -> new LinkedHashSet<>()).add(producer);
    }

    public Map<String, Set<Producer>> pull(Set<String> appNames) {
        Map<String, Set<Producer>> result = new HashMap<>();
        for (String appName : appNames) {
            result.put(appName, producers.get(appName));
        }
        return result;
    }

    public void heartbeat(Producer producer) {
        Set<Producer> producers = this.producers.get(producer.getAppName());
        if (producers != null) {
            producers.stream()
                    .filter(p -> p.equals(producer))
                    .findFirst()
                    .ifPresent(p -> p.setLastActiveMillis(System.currentTimeMillis()));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService.scheduleWithFixedDelay(() -> {
            producers.forEach((k, v) -> {
                Iterator<Producer> iterator = v.iterator();
                while (iterator.hasNext()) {
                    Producer producer = iterator.next();
                    if (Math.abs(System.currentTimeMillis() - producer.getLastActiveMillis()) > TimeUnit.MINUTES.toMillis(1L)) {
                        iterator.remove();
                        log.info("Remove a producer, appName: {}, host: {}, port: {}", producer.getAppName(), producer.getHost(), producer.getPort());
                    }
                }
            });
        }, 0L, 30L, TimeUnit.SECONDS);
    }
}
