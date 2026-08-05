package com.cscreativ.billboard.billboard.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class Pricing {
    private final BigDecimal dailyRate;
    private final String currency;

    public Pricing(BigDecimal dailyRate, String currency) {
        if (dailyRate == null || dailyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le tarif journalier est invalide");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("La devise est obligatoire");
        }
        this.dailyRate = dailyRate;
        this.currency = currency.toUpperCase().trim();
    }

    public BigDecimal getDailyRate() { return dailyRate; }
    public String getCurrency() { return currency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pricing pricing = (Pricing) o;
        return Objects.equals(dailyRate, pricing.dailyRate) && Objects.equals(currency, pricing.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dailyRate, currency);
    }
}
