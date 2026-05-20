package com.example.module.controller;

import com.example.module.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * EmailController - REST API endpoints for sending emails
 *
 * This controller provides endpoints to trigger email sending
 * through the EmailService
 */
@RestController
@RequestMapping("/email")
@Slf4j
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * POST /email/send - Send email with subject and message
     *
     * Example Request:
     * curl -X POST http://localhost:8080/email/send \
     *   -H "Content-Type: application/json" \
     *   -d '{"to":"jethajibabitaji8436@gmail.com","subject":"Test Mail","message":"Jevlas Kay"}'
     *
     * @param to Email recipient address
     * @param subject Email subject
     * @param message Email message body
     * @return Success or error message
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message) {
        try {
            log.info("Email send request received - To: {}, Subject: {}", to, subject);

            emailService.sendEmail(to, subject, message);

            log.info("Email sent successfully to: {}", to);
            return ResponseEntity.ok("Email sent successfully to: " + to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * POST /email/send-json - Send email using JSON request body
     *
     * Example Request:
     * curl -X POST http://localhost:8080/email/send-json \
     *   -H "Content-Type: application/json" \
     *   -d '{
     *     "to": "jethajibabitaji8436@gmail.com",
     *     "subject": "Test Mail",
     *     "message": "Jevlas Kay"
     *   }'
     *
     * @param emailRequest Contains to, subject, message
     * @return Success or error message
     */
    @PostMapping("/send-json")
    public ResponseEntity<?> sendEmailJson(@RequestBody EmailRequest emailRequest) {
        try {
            log.info("Email send (JSON) request received - To: {}, Subject: {}",
                    emailRequest.getTo(), emailRequest.getSubject());

            emailService.sendEmail(
                    emailRequest.getTo(),
                    emailRequest.getSubject(),
                    emailRequest.getMessage()
            );

            log.info("Email sent successfully to: {}", emailRequest.getTo());
            return ResponseEntity.ok("Email sent successfully to: " + emailRequest.getTo());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", emailRequest.getTo(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * GET /email/health - Check if email service is working
     *
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<?> emailHealth() {
        log.info("Email service health check");
        return ResponseEntity.ok("Email service is running");
    }

    /**
     * Email Request DTO
     */
    public static class EmailRequest {
        private String to;
        private String subject;
        private String message;

        // Constructors
        public EmailRequest() {}

        public EmailRequest(String to, String subject, String message) {
            this.to = to;
            this.subject = subject;
            this.message = message;
        }

        // Getters and Setters
        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

