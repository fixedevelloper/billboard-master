package com.cscreativ.billboard.mediabuyer.api.request;

import java.math.BigDecimal;

public record UpdateCreditLimitRequest(
        BigDecimal creditLimit
) {}
