package com.bourbon.service;

import com.bourbon.config.ConfigLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class BourbonService {

    private static final Logger log = LoggerFactory.getLogger(BourbonService.class);

    private final LinkedMultiValueMap<String, AsyncTask> contextMap = new LinkedMultiValueMap<>();

    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(8);

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private ObjectMapper objectMapper;

    public Map<String, Object> config(String filename, Boolean isAppName, String profile) {
        List<String> profiles = new ArrayList<>();
        if (StringUtils.hasText(profile)) {
            profiles = Arrays.asList(profile.split(","));
        }
        return configLoader.load(filename, isAppName, profiles);
    }

    public void listen(HttpServletRequest request, HttpServletResponse response) {
        String appName = request.getParameter("appName");
        AsyncContext context = request.startAsync(request, response);
        context.setTimeout(120000L);
        AsyncTask task = new AsyncTask(context, true);
        synchronized (this) {
            contextMap.add(appName, task);
        }
        executorService.schedule(() -> {
            if (task.getTimeout()) {
                synchronized (this) {
                    Objects.requireNonNull(contextMap.get(appName)).remove(task);
                }
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                context.complete();
            }
        }, 60L, TimeUnit.SECONDS);
    }

    public void publish(String appName, Boolean isAppName, String profile) {
        List<AsyncTask> taskList = contextMap.remove(appName);
        if (taskList != null) {
            Map<String, Object> configMap = config(appName, isAppName, profile);
            for (AsyncTask task : taskList) {
                task.setTimeout(false);
                HttpServletResponse response = (HttpServletResponse) task.getContext().getResponse();
                response.setStatus(HttpServletResponse.SC_OK);
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(objectMapper.writeValueAsString(configMap));
                } catch (Exception e) {
                    log.error("", e);
                }
                task.getContext().complete();
            }
        }
    }

    private static class AsyncTask {

        private AsyncContext context;
        private Boolean timeout;

        public AsyncTask(AsyncContext context, Boolean timeout) {
            this.context = context;
            this.timeout = timeout;
        }

        public AsyncContext getContext() {
            return context;
        }

        public void setContext(AsyncContext context) {
            this.context = context;
        }

        public Boolean getTimeout() {
            return timeout;
        }

        public void setTimeout(Boolean timeout) {
            this.timeout = timeout;
        }
    }
}
