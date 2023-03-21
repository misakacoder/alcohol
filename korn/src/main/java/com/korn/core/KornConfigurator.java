package com.korn.core;

import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.ElementSelector;
import ch.qos.logback.core.joran.spi.RuleStore;
import com.korn.action.EnvironmentAction;
import com.korn.action.PropertiesAction;

public class KornConfigurator extends JoranConfigurator {

    public void addInstanceRules(RuleStore ruleStore) {
        super.addInstanceRules(ruleStore);
        ruleStore.addRule(new ElementSelector("configuration/prop"), new PropertiesAction());
        ruleStore.addRule(new ElementSelector("configuration/env"), new EnvironmentAction());
    }
}
