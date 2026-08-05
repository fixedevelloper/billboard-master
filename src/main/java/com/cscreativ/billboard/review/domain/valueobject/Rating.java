package com.cscreativ.billboard.review.domain.valueobject;

import java.util.Objects;

public class Rating {
    private final int value; // Score de 1 à 5

    public Rating(int value) {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("La note doit être comprise entre 1 et 5 étoiles");
        }
        this.value = value;
    }

    public int getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return value == rating.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
