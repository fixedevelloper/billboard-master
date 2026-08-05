package com.cscreativ.billboard.reporting.domain;

import com.cscreativ.billboard.reporting.domain.valueobject.ReportMetrics;
import com.cscreativ.billboard.reporting.domain.valueobject.ReportPeriod;

import java.time.LocalDateTime;
import java.util.UUID;

public class PerformanceReport {
    private UUID id;
    private UUID targetId; // ID de la campagne, du panneau ou du régisseur
    private ReportType type;
    private ReportPeriod period;
    private ReportMetrics metrics;
    private LocalDateTime generatedAt;

    public PerformanceReport(UUID id, UUID targetId, ReportType type, ReportPeriod period, ReportMetrics metrics, LocalDateTime generatedAt) {
        this.id = id;
        this.targetId = targetId;
        this.type = type;
        this.period = period;
        this.metrics = metrics;
        this.generatedAt = generatedAt;
    }

    public static PerformanceReport generate(UUID targetId, ReportType type, ReportPeriod period, ReportMetrics metrics) {
        return new PerformanceReport(UUID.randomUUID(), targetId, type, period, metrics, LocalDateTime.now());
    }

    public UUID getId() { return id; }
    public UUID getTargetId() { return targetId; }
    public ReportType getType() { return type; }
    public ReportPeriod getPeriod() { return period; }
    public ReportMetrics getMetrics() { return metrics; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
}
