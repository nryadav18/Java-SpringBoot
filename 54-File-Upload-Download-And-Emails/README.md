# 54 - File Upload, Download and Emails

Upload files (single and multiple) safely, download them, show images in the browser, and send
plain-text and HTML emails with attachments. Open `lesson.html` for the full lesson.

## Files
| File | What it shows |
|------|---------------|
| `storage/FileStorageService.java` | Saving to disk safely: sanitizing names, extension whitelist, path-traversal guard |
| `web/FileController.java` | `MultipartFile` upload (single + batch), download with `Content-Disposition`, inline image view |
| `web/GlobalExceptionHandler.java` | 413 for too-large uploads, 400/404, 503 when the mail server is down (`ProblemDetail`) |
| `mail/EmailService.java` | `JavaMailSender`, `SimpleMailMessage`, `MimeMessage` + `MimeMessageHelper` with attachment |
| `mail/EmailController.java` | Email endpoints with validation |
| `config/StorageProperties.java`, `config/AppMailProperties.java` | Type-safe settings |
| `application.properties` | Multipart size limits, storage folder, Mailpit SMTP settings |
| `static/index.html` | A tiny upload page at http://localhost:8080 |
| `FileStorageServiceTest.java` | Unit tests for the security rules |
| `docker-compose.yml` | Mailpit fake SMTP server (SMTP 1025, inbox http://localhost:8025) |
| `sample-files/`, `requests.http` | Files and requests for trying it out |

## Run
```
mvn spring-boot:run          # uploads work right away (stored in ./uploads)
docker compose up -d         # only needed for the email endpoints
mvn test
```
