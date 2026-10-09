/*
 * The ROOT configuration class. It doesn't define many beans itself - it pulls
 * other configuration classes together with @Import.
 *
 * @Import             -> "also process these @Configuration classes" (explicit, no scanning needed).
 *                        Great for splitting configuration by topic: database, storage, features.
 * @PropertySource     -> load property files into the Environment. The SECOND file name contains a
 *                        placeholder: ${spring.profiles.active:dev} -> application-dev.properties or
 *                        application-prod.properties. (Spring Boot does this profile-specific loading
 *                        automatically - here we do it by hand to see the idea.)
 * @ComponentScan      -> find @Component classes in the "service" package.
 */
package com.course.configprofiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@Import({DevDataSourceConfig.class, ProdDataSourceConfig.class, StorageConfig.class, FeatureConfig.class})
@PropertySource("classpath:application.properties")
@PropertySource("classpath:application-${spring.profiles.active:dev}.properties")
@ComponentScan("com.course.configprofiles.service")
public class AppConfig {

    /** Strict ${...} resolution (Spring Boot registers this for you). Static - see Topic 24. */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
