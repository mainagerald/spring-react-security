package com.auth.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@masqani.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("noreply@masqani.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    // Verification email method
    public void sendVerificationEmail(String to, String verificationToken) throws MessagingException, jakarta.mail.MessagingException {
        String verificationUrl = "http://localhost:9900/api/auth/verify?token=" + verificationToken;

        String htmlBody =
                "<h1>Verify Your Email</h1>" +
                        "<p>Click the link below to verify your email address:</p>" +
                        "<a href='" + verificationUrl + "'>Verify Email</a>" +
                        "<p>If you did not create an account, please ignore this email.</p>";

        sendHtmlMessage(to, "Verify Your Email", htmlBody);
    }
}