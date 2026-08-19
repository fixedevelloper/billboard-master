package com.cscreativ.billboard.billboard.api.response;

import java.math.BigDecimal;
import java.util.UUID;

public record BillboardResponse(
        UUID id,
        String title,
        String description,
        String type,
        String status,
        String address,
        String city,
        double latitude,
        double longitude,
        double width,
        double height,
        BigDecimal dailyRate,
        String currency,
        String audience,
        Integer dailyTraffic,
        UUID ownerId
) {}
