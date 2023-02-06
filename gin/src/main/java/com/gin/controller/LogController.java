package com.gin.controller;

import com.gin.ro.LogLevelRO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Api(tags = "日志")
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @PostMapping("/level")
    @ApiOperation("修改日志级别")
    public void setLogLevel(@Validated @RequestBody LogLevelRO ro) {
        LoggingSystem loggingSystem = LogbackLoggingSystem.get(ClassLoader.getSystemClassLoader());
        List<String> loggerNameList = ro.getLoggerNameList();
        for (String loggerName : loggerNameList) {
            loggingSystem.setLogLevel(loggerName, ro.getLogLevel());
            scheduledExecutorService.schedule(() -> loggingSystem.setLogLevel(loggerName, LogLevel.INFO), ro.getTime(), TimeUnit.MINUTES);
        }
    }
}
