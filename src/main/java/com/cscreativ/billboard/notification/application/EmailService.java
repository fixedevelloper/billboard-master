package com.cscreativ.billboard.notification.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    EmailService(JavaMailSender mailSender, @Value("${app.mail.from:no-reply@guentours.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    boolean send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (Exception ex) {
            // A misconfigured/unreachable SMTP relay must never roll back the booking/payment
            // transaction that triggered this email - log and report failure to the caller
            // instead of throwing, so it can record it without aborting the wider transaction.
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
            return false;
        }
    }
}
