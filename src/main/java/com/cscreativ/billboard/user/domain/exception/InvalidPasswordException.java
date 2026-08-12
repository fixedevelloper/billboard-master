package com.cscreativ.billboard.user.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
