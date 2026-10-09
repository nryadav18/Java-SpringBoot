package com.course.files;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/*
 * Topic 54 - File Upload, Download and Emails
 * ------------------------------------------------------------
 * A small "document center":
 *   - upload one or many files      POST   /api/files , /api/files/batch
 *   - list / download / view        GET    /api/files , /api/files/{name} , /api/files/{name}/view
 *   - delete                        DELETE /api/files/{name}
 *   - send emails (plain text, HTML with attachment)   POST /api/emails/...
 *
 * Run:          mvn spring-boot:run     -> open http://localhost:8080 for a tiny upload page
 * For emails:   docker compose up -d    -> Mailpit catches all emails: http://localhost:8025
 *
 * @ConfigurationPropertiesScan finds our StorageProperties / AppMailProperties records (topic 29).
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class FilesApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilesApplication.class, args);
    }
}
