package com.cscreativ.billboard.reporting.infrastructure.persistence;

import com.cscreativ.billboard.reporting.domain.ReportType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "performance_reports")
public class PerformanceReportEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportType type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    private long totalImpressions;
    private long totalInteractions;
    private BigDecimal totalRevenue;
    private BigDecimal occupancyRate;

    private LocalDateTime generatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public ReportType getType() { return type; }
    public void setType(ReportType type) { this.type = type; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    public long getTotalImpressions() { return totalImpressions; }
    public void setTotalImpressions(long totalImpressions) { this.totalImpressions = totalImpressions; }
    public long getTotalInteractions() { return totalInteractions; }
    public void setTotalInteractions(long totalInteractions) { this.totalInteractions = totalInteractions; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(BigDecimal occupancyRate) { this.occupancyRate = occupancyRate; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
