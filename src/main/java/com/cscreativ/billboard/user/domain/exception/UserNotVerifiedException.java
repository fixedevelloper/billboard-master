package com.cscreativ.billboard.user.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class UserNotVerifiedException extends BusinessException {
    public UserNotVerifiedException() {
        super("Votre compte n'est pas encore vérifié. Consultez votre e-mail pour activer votre compte.");
    }
}
