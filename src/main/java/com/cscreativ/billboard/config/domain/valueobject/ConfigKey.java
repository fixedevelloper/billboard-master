package com.cscreativ.billboard.config.domain.valueobject;

import java.util.Objects;

public class ConfigKey {
    private final String value;

    public ConfigKey(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("La clé de configuration ne peut pas être vide");
        }
        this.value = value.trim().toUpperCase();
    }

    public String getValue() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigKey configKey = (ConfigKey) o;
        return Objects.equals(value, configKey.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
