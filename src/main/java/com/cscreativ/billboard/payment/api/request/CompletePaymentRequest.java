package com.cscreativ.billboard.payment.api.request;

public record CompletePaymentRequest(
        String gatewayReference
) {}
