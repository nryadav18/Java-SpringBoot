/*
 * Main configuration for the "QuickBite" food-delivery demo.
 *
 * @ComponentScan("com.course.di")   -> find all our @Component/@Service classes
 * @PropertySource(...)              -> load application.properties into Spring's Environment
 *                                      so ${...} placeholders in @Value can be resolved
 *
 * The static PropertySourcesPlaceholderConfigurer bean makes ${...} resolution STRICT:
 * an unknown key without a default causes a clear startup error instead of silently
 * injecting the text "${shop.typo}". (Spring Boot registers this bean for you.)
 * It must be STATIC because it is a special "BeanFactoryPostProcessor" that has to be
 * created before normal beans.
 */
package com.course.di.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan("com.course.di")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
