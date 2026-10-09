/*
 * Topic 66 - Docker for Spring Boot | docker-demo
 * ---------------------------------------------------------------
 * A deliberately SMALL app: the star of this topic is the Dockerfile, not the Java code.
 * The endpoints tell you WHERE the app runs (host name = container id inside Docker),
 * which Java version runs it and which configuration it received - perfect for proving
 * that environment variables passed with "docker run -e ..." really reach Spring.
 *
 * Run locally:   mvn spring-boot:run          -> http://localhost:8080/api/hello
 * Run in Docker: see README.md / lesson.html
 */
package com.course.docker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan   // finds AppProperties (a record with @ConfigurationProperties, topic 29)
public class DockerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerDemoApplication.class, args);
    }
}
