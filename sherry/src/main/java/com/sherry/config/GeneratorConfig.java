package com.sherry.config;

import com.sherry.util.DBUtil;
import com.sherry.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class GeneratorConfig {

    private static final Logger log = LoggerFactory.getLogger(GeneratorConfig.class);

    private String url;

    private String username;

    private String password;

    private String driver;

    private String author;

    private String module;

    private String databaseName;

    private String tableName;

    private String packageName;

    public GeneratorConfig(String configurationFile) {
        Properties properties = new Properties();
        File configuration = new File(configurationFile);
        try (InputStream is = configuration.exists() ? new FileInputStream(configuration) : FileUtil.getClasspathResource(configurationFile)) {
            properties.load(is);
            Field[] fields = GeneratorConfig.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = properties.getProperty(field.getName());
                if (value != null) {
                    field.set(this, value);
                }
            }
            databaseName = DBUtil.getDatabaseNameByUrl(url);
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    public String getAuthor() {
        return author;
    }

    public String getModule() {
        return module;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPackageName() {
        return packageName;
    }
}