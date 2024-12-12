package com.bourbon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bourbon.config.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/bourbon")
public class BourbonController {

    private final LinkedMultiValueMap<String, AsyncTask> contextMap = new LinkedMultiValueMap<>();
    private final ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(8);
    private static final Logger log = LoggerFactory.getLogger(BourbonController.class);

    @Autowired
    private ConfigLoader configLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/config/{filename}")
    public Map<String, Object> config(@PathVariable String filename, Boolean isAppName, String profile) {
        List<String> profiles = new ArrayList<>();
        if (StringUtils.hasText(profile)) {
            profiles = Arrays.asList(profile.split(","));
        }
        return configLoader.load(filename, isAppName, profiles);
    }

    @GetMapping("/config")
    public void config(HttpServletRequest request, HttpServletResponse response) {
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

    @GetMapping("/publish/config/{appName}")
    public void publish(@PathVariable String appName, Boolean isAppName, String profile) {
        Map<String, Object> configMap = config(appName, isAppName, profile);
        List<AsyncTask> taskList = contextMap.remove(appName);
        if (taskList != null) {
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
