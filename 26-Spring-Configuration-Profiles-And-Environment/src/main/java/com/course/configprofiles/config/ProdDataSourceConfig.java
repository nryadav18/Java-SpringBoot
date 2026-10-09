/*
 * CLASS-LEVEL @Profile("prod"): used only on the real server.
 * Exactly one of DevDataSourceConfig / ProdDataSourceConfig is active, so there is
 * always exactly ONE DatabaseSettings bean - no @Primary / @Qualifier needed.
 */
package com.course.configprofiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@Profile("prod")
public class ProdDataSourceConfig {

    @Bean
    public DatabaseSettings databaseSettings(Environment env) {
        System.out.println("   [config] ProdDataSourceConfig active -> real MySQL database");
        return new DatabaseSettings(
                env.getRequiredProperty("db.url"),
                env.getRequiredProperty("db.username"),
                env.getProperty("db.pool-size", Integer.class, 10));
    }
}
