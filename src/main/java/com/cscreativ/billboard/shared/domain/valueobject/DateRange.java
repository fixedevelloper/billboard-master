package com.cscreativ.billboard.shared.domain.valueobject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class DateRange {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public DateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public boolean overlaps(DateRange other) {
        return !this.endDate.isBefore(other.startDate) && !this.startDate.isAfter(other.endDate);
    }

    public long getDurationInDays() {
        return ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateRange dateRange = (DateRange) o;
        return Objects.equals(startDate, dateRange.startDate) && Objects.equals(endDate, dateRange.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
}
