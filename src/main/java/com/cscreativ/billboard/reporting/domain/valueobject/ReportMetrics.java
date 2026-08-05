package com.cscreativ.billboard.reporting.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class ReportMetrics {
    private final long totalImpressions;
    private final long totalInteractions;
    private final BigDecimal totalRevenue;
    private final BigDecimal occupancyRate;

    public ReportMetrics(long totalImpressions, long totalInteractions, BigDecimal totalRevenue, BigDecimal occupancyRate) {
        if (totalImpressions < 0 || totalInteractions < 0) {
            throw new IllegalArgumentException("Les métriques ne peuvent pas être négatives");
        }
        this.totalImpressions = totalImpressions;
        this.totalInteractions = totalInteractions;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.occupancyRate = occupancyRate != null ? occupancyRate : BigDecimal.ZERO;
    }

    public long getTotalImpressions() { return totalImpressions; }
    public long getTotalInteractions() { return totalInteractions; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getOccupancyRate() { return occupancyRate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportMetrics that = (ReportMetrics) o;
        return totalImpressions == that.totalImpressions &&
               totalInteractions == that.totalInteractions &&
               Objects.equals(totalRevenue, that.totalRevenue) &&
               Objects.equals(occupancyRate, that.occupancyRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalImpressions, totalInteractions, totalRevenue, occupancyRate);
    }
}
