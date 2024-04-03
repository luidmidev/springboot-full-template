package com.luidmidev.template.spring.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentChecker {

    private final Environment environment;

    public EnvironmentChecker(Environment environment) {
        this.environment = environment;
    }

    public boolean isProduction() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("prod")) return true;
        }
        return false;
    }
}
