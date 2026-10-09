package com.course.files.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.course.files.storage.StorageException;
import com.course.files.storage.StoredFileNotFoundException;

import jakarta.mail.MessagingException;

/*
 * Turns exceptions into clean ProblemDetail (RFC 9457) JSON responses - topic 35.
 * Note: we never send stack traces or full server paths to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** File bigger than spring.servlet.multipart.max-file-size / max-request-size -> 413 Payload Too Large */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail tooLarge(MaxUploadSizeExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.PAYLOAD_TOO_LARGE,
                "File too large. Maximum size per file is 5 MB, per request 20 MB.");
        problem.setTitle("Upload too large");
        return problem;
    }

    /** Request was not proper multipart, or the "file" part is missing */
    @ExceptionHandler(MultipartException.class)
    public ProblemDetail badMultipart(MultipartException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Expected a multipart/form-data request with a file part. " + ex.getMessage());
    }

    @ExceptionHandler(StorageException.class)
    public ProblemDetail storage(StorageException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setTitle("Upload rejected");
        return problem;
    }

    @ExceptionHandler(StoredFileNotFoundException.class)
    public ProblemDetail notFound(StoredFileNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** SMTP server down / wrong credentials -> 503 Service Unavailable with a helpful hint */
    @ExceptionHandler({MailException.class, MessagingException.class})
    public ProblemDetail mail(Exception ex) {
        log.error("Sending email failed: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE,
                "Could not send the email. Is the mail server running? Try: docker compose up -d (Mailpit on port 1025)");
        problem.setTitle("Mail server unavailable");
        return problem;
    }
}
