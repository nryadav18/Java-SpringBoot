package com.course.configprops;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/*
 * Topic 29 - Configuration Properties & Profiles
 * ----------------------------------------------
 * @ConfigurationPropertiesScan tells Spring Boot:
 *   "find every class/record annotated with @ConfigurationProperties in my packages
 *    and turn it into a bean filled with values from application.yml".
 *
 * How to run:
 *   mvn spring-boot:run                                   -> uses the "dev" profile (set in application.yml)
 *   mvn spring-boot:run -Dspring-boot.run.profiles=prod   -> uses the "prod" profile
 *   java -jar target/config-props-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --server.port=9000
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class ConfigPropsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigPropsApplication.class, args);
    }

    // A small CommandLineRunner (topic 28) that prints what configuration we ended up with.
    // Change the profile or pass --shop.name=Something and watch this output change.
    @Bean
    CommandLineRunner printConfig(ShopProperties shop, Environment env) {
        return args -> {
            System.out.println("----------------------------------------------------");
            System.out.println(" Active profiles : " + Arrays.toString(env.getActiveProfiles()));
            System.out.println(" Server port     : " + env.getProperty("server.port"));
            System.out.println(" Shop name       : " + shop.name());
            System.out.println(" Currency        : " + shop.currency());
            System.out.println(" Discount        : " + shop.discountPercent() + "%");
            System.out.println(" Payment methods : " + shop.supportedPaymentMethods());
            System.out.println("----------------------------------------------------");
        };
    }
}
