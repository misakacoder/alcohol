package com.korn.spi;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.OptionHelper;
import com.korn.core.KornConfigurator;
import com.korn.pattern.KornConsoleAppender;

import java.net.URL;
import java.util.List;

public class KornSpiConfigurator extends ContextAwareBase implements Configurator {

    final public static String AUTOCONFIG_FILE = "logback-korn.xml";
    final public static String CONFIG_FILE_PROPERTY = "korn.configurationFile";

    @Override
    public void configure(LoggerContext loggerContext) {
        loggerContext.stop();
        loggerContext.reset();
        doConfigure(loggerContext);
        clearStatusManager(loggerContext);
    }

    private void doConfigure(LoggerContext loggerContext) {
        KornConfigurator configurator = new KornConfigurator();
        configurator.setContext(loggerContext);
        String filepath = System.getProperty(CONFIG_FILE_PROPERTY);
        try {
            if (!OptionHelper.isEmpty(filepath)) {
                configurator.doConfigure(filepath);
                return;
            } else {
                URL url = ClassLoader.getSystemResource(AUTOCONFIG_FILE);
                if (url != null) {
                    configurator.doConfigure(url);
                    return;
                }
            }
        } catch (Exception e) {
            this.addError(e.getMessage());
        }
        loggerContext.getLogger("ROOT").addAppender(new KornConsoleAppender(loggerContext));
        loggerContext.getLogger(this.getClass()).warn("Use default configuration.");
    }

    private void clearStatusManager(LoggerContext loggerContext) {
        StatusManager statusManager = loggerContext.getStatusManager();
        List<Status> statusList = statusManager.getCopyOfStatusList();
        for (Status status : statusList) {
            int level = status.getLevel();
            Class<?> cls = status.getOrigin().getClass();
            String message = status.getMessage();
            Logger logger = loggerContext.getLogger(cls);
            switch (level) {
                case Status.INFO:
                    logger.info(message);
                    break;
                case Status.WARN:
                    logger.warn(message);
                    break;
                case Status.ERROR:
                    logger.error(message);
                    break;
            }
        }
        statusManager.clear();
    }
}
