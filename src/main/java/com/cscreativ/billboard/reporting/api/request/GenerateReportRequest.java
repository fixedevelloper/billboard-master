package com.cscreativ.billboard.reporting.api.request;

import com.cscreativ.billboard.reporting.domain.ReportType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GenerateReportRequest(
        UUID targetId,
        ReportType type,
        LocalDateTime startDate,
        LocalDateTime endDate,
        long totalImpressions,
        long totalInteractions,
        BigDecimal totalRevenue,
        BigDecimal occupancyRate
) {}
