/*
 * ConfigServerApplication.java  (Topic 62 - config-server)
 *
 * WHAT: One annotation, @EnableConfigServer, turns this Boot app into a configuration
 *       server. It reads files from a "config repository" (here: the local folder
 *       ../config-repo, in production usually a Git repository) and serves them as JSON:
 *
 *         GET http://localhost:8888/product-service/default   -> config for product-service
 *         GET http://localhost:8888/product-service/dev       -> same, with the "dev" profile
 *         GET http://localhost:8888/product-service-dev.yml   -> merged result as YAML
 *
 * RUN:  cd config-server            (IMPORTANT: run from this folder, so ../config-repo is found)
 *       mvn spring-boot:run
 *
 * START ORDER: config-server FIRST, then product-service.
 */
package com.course.configdemo.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
