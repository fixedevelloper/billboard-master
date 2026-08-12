package com.cscreativ.billboard.user.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class InvalidVerificationTokenException extends BusinessException {
    public InvalidVerificationTokenException() {
        super("Code de vérification invalide.");
    }
}
