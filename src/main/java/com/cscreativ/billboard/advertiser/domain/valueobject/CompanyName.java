package com.cscreativ.billboard.advertiser.domain.valueobject;

import java.util.Objects;

public class CompanyName {
    private final String value;

    public CompanyName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le nom de la compagnie ne peut pas être vide");
        }
        this.value = value.trim();
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyName that = (CompanyName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
