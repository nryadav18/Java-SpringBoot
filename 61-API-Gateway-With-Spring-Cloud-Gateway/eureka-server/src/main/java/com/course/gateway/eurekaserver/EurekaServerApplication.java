/*
 * EurekaServerApplication.java  (Topic 61 - eureka-server)
 *
 * WHAT: A complete service registry in ONE annotation: @EnableEurekaServer.
 *       Services register here ("I am product-service, I live at localhost:8081")
 *       and look up others here ("where is product-service?").
 *
 * RUN:  cd eureka-server
 *       mvn spring-boot:run
 *       Open the dashboard: http://localhost:8761
 *
 * START ORDER: ALWAYS start this one FIRST, then product-service and order-service, and api-gateway LAST.
 */
package com.course.gateway.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer   // turns this ordinary Boot app into a Eureka registry + dashboard
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
