/*
 * CLASS-LEVEL @Profile: this whole configuration class (and every bean in it) is used
 * ONLY when the "dev" profile is active.
 *
 * It also shows reading settings through the Environment object instead of @Value:
 *   env.getRequiredProperty("db.url")             -> fails clearly if missing
 *   env.getProperty("db.pool-size", Integer.class) -> converts the text to Integer
 */
package com.course.configprofiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("dev")
public class DevDataSourceConfig {

    @Bean
    public DatabaseSettings databaseSettings(Environment env) {
        System.out.println("   [config] DevDataSourceConfig active -> in-memory H2 database");
        return new DatabaseSettings(
                env.getRequiredProperty("db.url"),
                env.getRequiredProperty("db.username"),
                env.getProperty("db.pool-size", Integer.class, 2));
    }
}
