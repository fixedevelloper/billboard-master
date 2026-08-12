package com.cscreativ.billboard.user.domain.exception;

import com.cscreativ.billboard.shared.domain.exception.BusinessException;

public class UserDisabledException extends BusinessException {
    public UserDisabledException(String message) {
        super(message);
    }
}
