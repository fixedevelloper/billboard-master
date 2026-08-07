package com.cscreativ.billboard.payment.api.request;

public record FlutterwaveCheckoutRequest(
        String customerEmail,
        String customerName,
        String customerPhone
) {}
