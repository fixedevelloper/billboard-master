package com.cscreativ.billboard.reporting.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PerformanceReportResponse(
        UUID id,
        UUID targetId,
        String type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        long totalImpressions,
        long totalInteractions,
        BigDecimal totalRevenue,
        BigDecimal occupancyRate,
        LocalDateTime generatedAt
) {}
