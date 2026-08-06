package com.cscreativ.billboard.platformsettings.domain;

import com.cscreativ.billboard.platformsettings.domain.valueobject.ConfigKey;

import java.time.LocalDateTime;

public class PlatformSetting {
    private ConfigKey key;
    private String value;
    private String description;
    private ConfigType type;
    private LocalDateTime updatedAt;

    public PlatformSetting(ConfigKey key, String value, String description, ConfigType type, LocalDateTime updatedAt) {
        this.key = key;
        this.value = value;
        this.description = description;
        this.type = type;
        this.updatedAt = updatedAt;
    }

    public static PlatformSetting create(ConfigKey key, String value, String description, ConfigType type) {
        return new PlatformSetting(key, value, description, type, LocalDateTime.now());
    }

    public void updateValue(String newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException("La valeur ne peut pas être nulle");
        }
        this.value = newValue;
        this.updatedAt = LocalDateTime.now();
    }

    public ConfigKey getKey() { return key; }
    public String getValue() { return value; }
    public String getDescription() { return description; }
    public ConfigType getType() { return type; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
