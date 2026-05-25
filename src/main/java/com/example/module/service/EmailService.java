package com.example.module.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService - Service for sending emails via Gmail SMTP
 *
 * This service uses Spring's JavaMailSender to send emails through Gmail's SMTP server.
 *
 * Configuration in application.yml:
 * - Host: smtp.gmail.com (Gmail's SMTP server)
 * - Port: 587 (TLS port)
 * - Username: Gmail email address
 * - Password: Gmail app password (not regular password)
 * - STARTTLS: Encrypts connection after initial connection
 *
 * Important Notes:
 * 1. Use app password, not regular Gmail password
 * 2. Enable 2-factor authentication on Gmail
 * 3. Generate app password in Gmail Security settings
 * 4. Store credentials in environment variables in production
 */
@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send simple email
     *
     * @param to Recipient email address
     * @param subject Email subject
     * @param body Email message body
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Send email with cc
     *
     * @param to Recipient email address
     * @param cc CC recipients
     * @param subject Email subject
     * @param body Email message body
     */
    public void sendEmailWithCc(String to, String[] cc, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setCc(cc);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            log.info("Email with CC sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email with CC to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Send email with cc and bcc
     *
     * @param to Recipient email address
     * @param cc CC recipients
     * @param bcc BCC recipients
     * @param subject Email subject
     * @param body Email message body
     */
    public void sendEmailWithCcBcc(String to, String[] cc, String[] bcc, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setCc(cc);
            message.setBcc(bcc);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);
            log.info("Email with CC and BCC sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email with CC and BCC to: {}", to, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}

