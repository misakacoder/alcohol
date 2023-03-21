package com.korn.action;

import ch.qos.logback.core.joran.action.Action;
import ch.qos.logback.core.joran.action.ActionUtil;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import ch.qos.logback.core.util.OptionHelper;
import org.xml.sax.Attributes;

public abstract class BaseAction extends Action {

    private static final String DEFAULT_VALUE_ATTRIBUTE = "defaultValue";

    @Override
    public void begin(InterpretationContext context, String elementName, Attributes attributes) throws ActionException {
        String name = attributes.getValue(NAME_ATTRIBUTE);
        String key = attributes.getValue(KEY_ATTRIBUTE);
        ActionUtil.Scope scope = ActionUtil.stringToScope(attributes.getValue(SCOPE_ATTRIBUTE));
        String defaultValue = attributes.getValue(DEFAULT_VALUE_ATTRIBUTE);
        if (OptionHelper.isEmpty(name) || OptionHelper.isEmpty(key)) {
            addError("The \"name\" and \"key\" attributes of " + elementName + " must be set");
        } else {
            String value = getValue(key);
            value = !OptionHelper.isEmpty(value) ? value : defaultValue;
            ActionUtil.setProperty(context, name, value, scope);
        }
    }

    public abstract String getValue(String key);

    @Override
    public void end(InterpretationContext context, String name) throws ActionException {

    }
}
