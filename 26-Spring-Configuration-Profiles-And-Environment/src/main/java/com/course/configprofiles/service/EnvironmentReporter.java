/*
 * The Environment - Spring's single "settings desk".
 *
 * It answers two kinds of questions:
 *   1. PROFILES   -> getActiveProfiles(), getDefaultProfiles(), acceptsProfiles(...)
 *   2. PROPERTIES -> getProperty(...), containsProperty(...), getRequiredProperty(...)
 *
 * Properties come from several PROPERTY SOURCES, searched in order:
 *   JVM system properties (-Dkey=value)  ->  OS environment variables  ->  @PropertySource files
 * The first source that has the key wins.
 */
package com.course.configprofiles.service;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvironmentReporter {

    private final Environment env;

    public EnvironmentReporter(Environment env) {   // the Environment is injectable like any bean
        this.env = env;
    }

    public void report() {
        System.out.println("   active profiles : " + Arrays.toString(env.getActiveProfiles()));
        System.out.println("   default profiles: " + Arrays.toString(env.getDefaultProfiles()));
        System.out.println("   is 'prod' active? " + env.acceptsProfiles(Profiles.of("prod")));
        System.out.println("   app.greeting    : " + env.getProperty("app.greeting"));
        System.out.println("   shop.max-items  : " + env.getProperty("shop.max-items", Integer.class));
        System.out.println("   shop.currency   : " + env.getProperty("shop.currency", "INR") + "  (missing -> default)");
        System.out.println("   java.version    : " + env.getProperty("java.version") + "  (a JVM system property)");
        System.out.println("   has PATH env var? " + env.containsProperty("PATH") + "  (an OS environment variable)");

        if (env instanceof ConfigurableEnvironment configurable) {   // pattern matching instanceof (Topic 15)
            System.out.println("   property sources, in search order:");
            for (PropertySource<?> source : configurable.getPropertySources()) {
                System.out.println("     - " + source.getName());
            }
        }
    }
}
