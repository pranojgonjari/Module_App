package com.example.module;

import com.example.module.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * EmailServiceTest - Unit tests for EmailService
 *
 * Uses:
 * - @ExtendWith(MockitoExtension.class) - JUnit 5 Mockito integration
 * - @Mock - Creates mock instance of JavaMailSender
 * - @InjectMocks - Injects mocked dependencies into EmailService
 * - ArgumentCaptor - Capture arguments passed to mock methods
 * - verify() - Assert method calls
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailService Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    private static final String FROM_EMAIL = "jethajibabitaji8436@gmail.com";
    private static final String TO_EMAIL = "jethajibabitaji8436@gmail.com";
    private static final String TEST_SUBJECT = "Test Mail";
    private static final String TEST_BODY = "Jevlas Kay";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "fromEmail", FROM_EMAIL);
    }

    @Test
    @DisplayName("Should send email successfully")
    void testSendEmailSuccess() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail(TO_EMAIL, TEST_SUBJECT, TEST_BODY));

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send email with correct details")
    void testSendEmailWithCorrectDetails() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(TO_EMAIL, TEST_SUBJECT, TEST_BODY);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals(FROM_EMAIL, message.getFrom());
        assertEquals(TO_EMAIL, message.getTo()[0]);
        assertEquals(TEST_SUBJECT, message.getSubject());
        assertEquals(TEST_BODY, message.getText());
    }

    @Test
    @DisplayName("Should throw exception when sending email fails")
    void testSendEmailFailure() {
        doThrow(new RuntimeException("SMTP connection failed"))
                .when(javaMailSender).send(any(SimpleMailMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> emailService.sendEmail(TO_EMAIL, TEST_SUBJECT, TEST_BODY));

        assertTrue(exception.getMessage().contains("Failed to send email"));
    }

    @Test
    @DisplayName("Should send email with CC recipients")
    void testSendEmailWithCc() {
        String[] ccRecipients = {"cc@example.com"};
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmailWithCc(TO_EMAIL, ccRecipients, TEST_SUBJECT, TEST_BODY);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertNotNull(message.getCc());
        assertEquals("cc@example.com", message.getCc()[0]);
    }

    @Test
    @DisplayName("Should send email with CC and BCC recipients")
    void testSendEmailWithCcAndBcc() {
        String[] ccRecipients = {"cc@example.com"};
        String[] bccRecipients = {"bcc@example.com"};
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmailWithCcBcc(TO_EMAIL, ccRecipients, bccRecipients, TEST_SUBJECT, TEST_BODY);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertNotNull(message.getCc());
        assertEquals("cc@example.com", message.getCc()[0]);
        assertNotNull(message.getBcc());
        assertEquals("bcc@example.com", message.getBcc()[0]);
    }
}

