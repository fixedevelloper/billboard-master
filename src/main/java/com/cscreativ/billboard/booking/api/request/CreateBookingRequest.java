package com.cscreativ.billboard.booking.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBookingRequest(
        UUID billboardId,
        UUID advertiserId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal dailyRate,
        String currency
) {}
