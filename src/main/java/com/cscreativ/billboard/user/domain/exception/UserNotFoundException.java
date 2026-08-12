package com.cscreativ.billboard.user.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
