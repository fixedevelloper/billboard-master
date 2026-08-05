package com.cscreativ.billboard.user.domain.valueobject;

public class Password {
    private final String hashedValue;

    public Password(String hashedValue) {
        if (hashedValue == null || hashedValue.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        this.hashedValue = hashedValue;
    }

    public String getHashedValue() {
        return hashedValue;
    }
}
