package com.korn.core;

import ch.qos.logback.core.PropertyDefinerBase;

public class PidPropertyDefiner extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        return Long.toString(ProcessHandle.current().pid());
    }
}
