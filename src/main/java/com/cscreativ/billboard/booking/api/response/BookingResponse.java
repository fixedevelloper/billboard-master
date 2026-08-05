package com.cscreativ.billboard.booking.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID billboardId,
        UUID advertiserId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        String currency,
        String status
) {}
