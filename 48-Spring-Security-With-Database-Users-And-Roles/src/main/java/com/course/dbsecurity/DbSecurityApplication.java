package com.course.dbsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Topic 48 - Spring Security with Database Users and Roles
 * --------------------------------------------------------
 * A "Personal Notes" API. Users register, log in with HTTP Basic, and keep private notes.
 *
 *   POST   /api/auth/register            -> anyone: create an account (role USER)
 *   GET    /api/me                        -> logged-in: who am I? (@AuthenticationPrincipal)
 *   GET    /api/me/context                -> same info, read from SecurityContextHolder
 *   POST   /api/notes                     -> logged-in: add a note that I own
 *   GET    /api/users/{username}/notes    -> owner OR admin (@PreAuthorize with SpEL)
 *   GET    /api/admin/users               -> ADMIN only (URL rule in SecurityConfig)
 *   DELETE /api/admin/users/{id}          -> ADMIN only (@PreAuthorize on the service)
 *   GET    /api/reports/summary           -> needs authority "REPORT_READ" (hasAuthority)
 *
 * Seeded users (DataSeeder): admin / admin123 (ADMIN, USER), alice / alice123 (USER)
 * H2 console: http://localhost:8080/h2-console  (JDBC URL jdbc:h2:mem:notesdb, user sa, no password)
 *
 * Run:  mvn spring-boot:run
 */
@SpringBootApplication
public class DbSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbSecurityApplication.class, args);
    }
}
