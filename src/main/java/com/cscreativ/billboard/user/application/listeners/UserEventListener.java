package com.cscreativ.billboard.user.application.listeners;

import com.cscreativ.billboard.user.events.UserRegisteredEvent;
import com.cscreativ.billboard.user.events.VerificationEmailRequestedEvent;
import com.cscreativ.billboard.user.infrastructure.mail.VerificationMailSender;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener {

    private final VerificationMailSender mailSender;

    public UserEventListener(VerificationMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @ApplicationModuleListener
    public void onUserRegistered(UserRegisteredEvent event) {
        mailSender.sendVerificationEmail(event.email(), event.verificationToken());
    }

    @ApplicationModuleListener
    public void onVerificationEmailRequested(VerificationEmailRequestedEvent event) {
        mailSender.sendVerificationEmail(event.email(), event.verificationToken());
    }
}
