package com.misaka.support;

import feign.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.StringJoiner;

public class Slf4jLogger extends Logger {

    private static final String REQUEST_END = "<--- END HTTP";
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Slf4jLogger.class);

    private final ThreadLocal<StringJoiner> requestLogThreadLocal = ThreadLocal.withInitial(() -> new StringJoiner("\n"));

    @Override
    protected void log(String configKey, String format, Object... args) {
        if (log.isDebugEnabled()) {
            if (StringUtils.hasText(format)) {
                String formatted = String.format(format, args);
                if (!formatted.contains(REQUEST_END)) {
                    requestLogThreadLocal.get().add(formatted);
                } else {
                    String requestLog = requestLogThreadLocal.get().add(formatted).toString();
                    requestLogThreadLocal.remove();
                    log.debug("method: {}, request: \n{}", configKey, requestLog);
                }
            }
        }
    }
}
