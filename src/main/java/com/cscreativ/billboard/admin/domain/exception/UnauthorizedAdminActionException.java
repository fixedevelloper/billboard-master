package com.cscreativ.billboard.admin.domain.exception;

public class UnauthorizedAdminActionException extends RuntimeException {
    public UnauthorizedAdminActionException(String message) {
        super(message);
    }
}
