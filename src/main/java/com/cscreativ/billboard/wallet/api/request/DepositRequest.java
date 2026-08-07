package com.cscreativ.billboard.wallet.api.request;

import java.math.BigDecimal;

public record DepositRequest(BigDecimal amount, String currency, String reference) {}
