package com.course.files.mail;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.course.files.config.AppMailProperties;
import com.course.files.storage.FileStorageService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/*
 * Sending emails with Spring's JavaMailSender.
 *
 * JavaMailSender is auto-configured by Spring Boot from the spring.mail.* properties
 * (host, port, username, password ...). It talks SMTP - the protocol mail servers use.
 *
 * Two kinds of message:
 *   SimpleMailMessage - plain text only. Very easy.
 *   MimeMessage       - the "full" email: HTML, attachments, inline images. Built with MimeMessageHelper.
 *
 * For learning we send everything to MAILPIT (docker-compose.yml) - a fake SMTP server that catches
 * every email and shows it in a web inbox at http://localhost:8025. No real emails leave your machine.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final FileStorageService storageService;
    private final String from;

    public EmailService(JavaMailSender mailSender, FileStorageService storageService, AppMailProperties mailProperties) {
        this.mailSender = mailSender;
        this.storageService = storageService;
        this.from = mailProperties.from();
    }

    /** Plain-text email. */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);   // throws MailException if the SMTP server cannot be reached
        log.info("Plain-text email sent to {}", to);
    }

    /**
     * HTML email with an optional attachment taken from our upload folder.
     *
     * MimeMessageHelper(message, true, "UTF-8"):
     *   true    -> "multipart" message, needed for attachments and inline images
     *   "UTF-8" -> so names like "Zoë" or "₹" are not broken
     */
    public void sendHtmlEmailWithAttachment(String to, String subject, String html, String storedFileName)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        // setText(plainTextFallback, html): clients that cannot show HTML show the plain text
        helper.setText("Please open this email in an HTML-capable mail client.", html);

        if (storedFileName != null && !storedFileName.isBlank()) {
            Path file = storageService.pathOf(storedFileName);   // safe lookup inside the upload folder
            helper.addAttachment(FileStorageService.originalNameOf(storedFileName), new FileSystemResource(file));
        }

        mailSender.send(message);
        log.info("HTML email sent to {} (attachment: {})", to, storedFileName);
    }
}
