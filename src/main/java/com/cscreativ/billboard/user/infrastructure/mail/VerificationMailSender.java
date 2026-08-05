package com.cscreativ.billboard.user.infrastructure.mail;

import org.springframework.stereotype.Component;

@Component
public class VerificationMailSender {
    public void sendVerificationEmail(String email, String token) {
        System.out.println("Envoi d'email de vérification à " + email + " avec token : " + token);
    }
}
