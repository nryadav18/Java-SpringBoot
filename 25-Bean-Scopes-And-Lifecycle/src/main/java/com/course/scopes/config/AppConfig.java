/*
 * Configuration for Topic 25.
 *
 * - Scans com.course.scopes for components (catalog, cart, report, the BeanPostProcessor).
 * - Registers DatabaseConnectionPool with a @Bean method so we can name a custom
 *   initMethod and destroyMethod (handy for library classes you cannot annotate).
 */
package com.course.scopes.config;

import com.course.scopes.lifecycle.DatabaseConnectionPool;
import com.course.scopes.lifecycle.PoolSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.course.scopes")
public class AppConfig {

    @Bean
    public PoolSettings poolSettings() {
        return new PoolSettings(5);
    }

    @Bean(initMethod = "openConnections", destroyMethod = "closeConnections")
    public DatabaseConnectionPool databaseConnectionPool() {
        return new DatabaseConnectionPool("jdbc:h2:mem:shop");
    }
}
