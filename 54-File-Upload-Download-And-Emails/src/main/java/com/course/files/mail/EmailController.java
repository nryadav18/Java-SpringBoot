package com.course.files.mail;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * POST /api/emails/simple           -> plain text email
 * POST /api/emails/with-attachment  -> HTML email + a previously uploaded file as attachment
 *
 * Start Mailpit first (docker compose up -d) and open http://localhost:8025 to see the emails.
 * Without Mailpit you get a clear 503 error from GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/emails")
public class EmailController {

    public record SimpleEmailRequest(@NotBlank @Email String to, @NotBlank String subject, @NotBlank String text) { }

    public record AttachmentEmailRequest(@NotBlank @Email String to, @NotBlank String subject,
                                         @NotBlank String html, String storedFileName) { }

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/simple")
    public ResponseEntity<Map<String, String>> sendSimple(@Valid @RequestBody SimpleEmailRequest request) {
        emailService.sendSimpleEmail(request.to(), request.subject(), request.text());
        return ResponseEntity.ok(Map.of("status", "sent", "to", request.to()));
    }

    @PostMapping("/with-attachment")
    public ResponseEntity<Map<String, String>> sendWithAttachment(@Valid @RequestBody AttachmentEmailRequest request)
            throws MessagingException {
        emailService.sendHtmlEmailWithAttachment(request.to(), request.subject(), request.html(), request.storedFileName());
        return ResponseEntity.ok(Map.of("status", "sent", "to", request.to()));
    }
}
