package com.cscreativ.billboard.billboard.api.request;

import com.cscreativ.billboard.billboard.domain.BillboardType;
import java.math.BigDecimal;

public record UpdateBillboardRequest(
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
        String currency
) {}
