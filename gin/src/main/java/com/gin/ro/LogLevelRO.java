package com.gin.ro;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.boot.logging.LogLevel;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("日志级别RO")
public class LogLevelRO {

    @NotNull
    @ApiModelProperty("日志级别")
    private LogLevel logLevel;

    @NotNull
    @Min(1)
    @Max(24 * 60)
    @ApiModelProperty("激活时间")
    private Long time;

    @NotEmpty
    @ApiModelProperty("类列表")
    private List<String> loggerNameList;

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public List<String> getLoggerNameList() {
        return loggerNameList;
    }

    public void setLoggerNameList(List<String> loggerNameList) {
        this.loggerNameList = loggerNameList;
    }
}
