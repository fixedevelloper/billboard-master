package com.cscreativ.billboard.owner.api.request;

import java.math.BigDecimal;

public record UpdateRevenueShareRequest(
        BigDecimal revenueShareRate
) {}
