package com.cscreativ.billboard.installation.domain.exception;

public class InstallationTaskNotFoundException extends RuntimeException {
    public InstallationTaskNotFoundException(String message) {
        super(message);
    }
}
