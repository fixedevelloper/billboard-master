package com.cscreativ.billboard.payment.domain.exception;

public class PaymentTransactionNotFoundException extends RuntimeException {
    public PaymentTransactionNotFoundException(String message) {
        super(message);
    }
}
