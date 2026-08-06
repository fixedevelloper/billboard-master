package com.cscreativ.billboard.user.infrastructure.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class
VerificationMailSender {

    private final JavaMailSender mailSender;
    private final String from;

    public VerificationMailSender(JavaMailSender mailSender, @Value("${mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendVerificationEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Vérifiez votre compte Billboard");
        message.setText("""
                Bonjour,

                Voici votre code de vérification : %s

                Si vous n'êtes pas à l'origine de cette demande, ignorez cet e-mail.
                """.formatted(token));
        mailSender.send(message);
    }
}
