package com.cscreativ.billboard.user.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class VerificationTokenExpiredException extends BusinessException {
    public VerificationTokenExpiredException() {
        super("Ce code de vérification a expiré. Demandez-en un nouveau.");
    }
}
