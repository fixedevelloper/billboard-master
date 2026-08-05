package com.cscreativ.billboard.advertiser.domain.valueobject;

import java.util.Objects;

public class TaxNumber {
    private final String value;

    public TaxNumber(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le numéro fiscal (NIU / SIRET) ne peut pas être vide");
        }
        this.value = value.trim().toUpperCase();
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaxNumber taxNumber = (TaxNumber) o;
        return Objects.equals(value, taxNumber.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
