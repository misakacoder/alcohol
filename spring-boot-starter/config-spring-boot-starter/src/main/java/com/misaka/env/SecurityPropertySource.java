package com.misaka.env;

import com.misaka.util.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SecurityPropertySource extends PropertySource<PropertySource<?>> {

    private static final Pattern SECURITY_PATTERN = Pattern.compile("ENC\\((.*?)\\)");
    private static final Logger log = LoggerFactory.getLogger(SecurityPropertySource.class);

    private final String password;

    public SecurityPropertySource(String password, String name, PropertySource<?> source) {
        super(name, source);
        this.password = password;
    }

    @Override
    public Object getProperty(String name) {
        Object property = source.getProperty(name);
        if (property != null) {
            String value = property.toString();
            Matcher matcher = SECURITY_PATTERN.matcher(value);
            if (matcher.find()) {
                String ciphertext = matcher.group(1);
                try {
                    return AESUtil.decrypt(password, ciphertext);
                } catch (Exception e) {
                    log.error("", e);
                    throw new RuntimeException(e);
                }
            }
        }
        return property;
    }
}
