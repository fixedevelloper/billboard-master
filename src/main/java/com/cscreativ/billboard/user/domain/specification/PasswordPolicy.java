package com.cscreativ.billboard.user.domain.specification;

public class PasswordPolicy {
    private static final int MIN_LENGTH = 8;

    public static void validateRawPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins " + MIN_LENGTH + " caractères.");
        }
        if (!rawPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins une lettre majuscule.");
        }
        if (!rawPassword.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins un chiffre.");
        }
    }
}
