package com.cscreativ.billboard.billboard.api.request;

import com.cscreativ.billboard.billboard.domain.BillboardType;
import java.math.BigDecimal;
import java.util.UUID;

public record CreateBillboardRequest(
        String title,
        String description,
        BillboardType type,
        String address,
        String city,
        double latitude,
        double longitude,
        double width,
        double height,
        BigDecimal dailyRate,
        String currency,
        UUID ownerId
) {}
