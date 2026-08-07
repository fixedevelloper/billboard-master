package com.cscreativ.billboard.wallet.api.request;

import java.math.BigDecimal;

public record WithdrawRequest(BigDecimal amount, String currency, String reference) {}
