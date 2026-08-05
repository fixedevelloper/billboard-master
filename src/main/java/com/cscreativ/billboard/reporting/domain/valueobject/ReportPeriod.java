package com.cscreativ.billboard.reporting.domain.valueobject;

import java.time.LocalDateTime;
import java.util.Objects;

public class ReportPeriod {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public ReportPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("La date de début ne peut pas être postérieure à la date de fin");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportPeriod period = (ReportPeriod) o;
        return Objects.equals(startDate, period.startDate) &&
               Objects.equals(endDate, period.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
}
