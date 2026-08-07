package com.cscreativ.billboard.payment.infrastructure.gateway;

public class FlutterwaveException extends RuntimeException {

    public FlutterwaveException(String message) {
        super(message);
    }

    public FlutterwaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
