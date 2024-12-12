package com.bourbon.loader;

import com.bourbon.consts.BourbonConsts;
import com.bourbon.properties.BourbonProperties;
import org.springframework.boot.context.config.*;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.Ordered;

import java.util.List;

public class BourbonConfigDataLocationResolver implements ConfigDataLocationResolver<BourbonConfigDataResource>, Ordered {

    private static final String PREFIX = "bourbon:";
    private static final String PROFILE_PREFIX = "profile=";
    private static final String ENABLED_PROPERTY_NAME = "bourbon.enabled";

    @Override
    public boolean isResolvable(ConfigDataLocationResolverContext context, ConfigDataLocation location) {
        return location.hasPrefix(PREFIX) ? context.getBinder().bind(ENABLED_PROPERTY_NAME, Boolean.class).orElse(false) : false;
    }

    @Override
    public List<BourbonConfigDataResource> resolve(ConfigDataLocationResolverContext context, ConfigDataLocation location) throws ConfigDataLocationNotFoundException, ConfigDataResourceNotFoundException {
        String value = location.getNonPrefixedValue(PREFIX);
        String filename = value.substring(0, value.indexOf("?"));
        String profile = null;
        int index = value.indexOf(PROFILE_PREFIX);
        if (index > 0) {
            profile = value.substring(index + PROFILE_PREFIX.length());
        }
        Binder binder = context.getBinder();
        BourbonProperties bourbonProperties = binder.bind("bourbon", Bindable.of(BourbonProperties.class)).orElseGet(BourbonProperties::new);
        String appName = binder.bind(BourbonConsts.APPLICATION_NAME_PROPERTY_NAME, String.class).orElse("");
        return List.of(new BourbonConfigDataResource(bourbonProperties, filename, appName.equals(filename), profile));
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
