package com.example.module.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * MailSenderConfig - Configuration for JavaMailSender Bean
 *
 * This configuration explicitly sets up the JavaMailSender bean
 * with Gmail SMTP properties from application.yml
 */
@Configuration
public class MailSenderConfig {

    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private int port;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${mail.smtp.password}")
    private String password;

    @Value("${mail.smtp.auth}")
    private boolean auth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${mail.smtp.starttls.required}")
    private boolean starttlsRequired;

    @Value("${mail.smtp.connectiontimeout}")
    private int connectionTimeout;

    @Value("${mail.smtp.timeout}")
    private int timeout;

    @Value("${mail.smtp.writetimeout}")
    private int writeTimeout;

    /**
     * JavaMailSender Bean
     *
     * @return Configured JavaMailSender instance
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Set host and port
        mailSender.setHost(host);
        mailSender.setPort(port);

        // Set credentials
        mailSender.setUsername(username);
        mailSender.setPassword(password);

        // Set email properties
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.starttls.required", starttlsRequired);
        props.put("mail.smtp.connectiontimeout", connectionTimeout);
        props.put("mail.smtp.timeout", timeout);
        props.put("mail.smtp.writetimeout", writeTimeout);

        return mailSender;
    }
}

