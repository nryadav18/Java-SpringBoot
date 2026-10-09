# 48 - Spring Security with Database Users and Roles

A "Personal Notes" REST API whose users and roles live in an H2 database (JPA).
Registration hashes passwords with BCrypt; access is controlled with URL rules
(`hasRole`, `hasAuthority`) and method security (`@PreAuthorize` with SpEL). Open **lesson.html**.

## Files (under `src/main/java/com/course/dbsecurity`)
| File | What it shows |
|------|---------------|
| `user/AppUser.java`, `user/Role.java` | User entity + roles (each role carries permissions) |
| `user/AppUserRepository.java` | `findByUsername` used at login |
| `security/SecurityUser.java` | Adapter: `AppUser` -> `UserDetails` |
| `security/JpaUserDetailsService.java` | Loads users from the database at login |
| `security/SecurityConfig.java` | URL rules, HTTP Basic, stateless, `@EnableMethodSecurity`, BCrypt |
| `user/UserService.java` | Registration with BCrypt, `@PreAuthorize` delete |
| `note/NoteService.java` | `@PreAuthorize("#username == authentication.name or hasRole('ADMIN')")` |
| `web/*Controller.java` | Register, `/api/me` (`@AuthenticationPrincipal` + `SecurityContextHolder`), notes, admin |
| `config/DataSeeder.java` | Seeds `admin/admin123` and `alice/alice123` |
| `src/test/...` | `@WithMockUser`, `@WithUserDetails`, `httpBasic()` tests |
| `requests.http` | Ready-made requests |

## Run
```
mvn spring-boot:run
curl -u alice:alice123 http://localhost:8080/api/me
mvn test
```
H2 console: http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:notesdb`, user `sa`).
