package com.cscreativ.billboard.user.events;

import java.time.LocalDateTime;
import java.util.UUID;

/** Publié lorsqu'un nouveau code de vérification doit être envoyé (renvoi demandé par l'utilisateur). */
public record VerificationEmailRequestedEvent(
        UUID userId,
        String email,
        String verificationToken,
        LocalDateTime occurredOn
) {}
