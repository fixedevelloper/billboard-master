package com.cscreativ.billboard.platformsettings.infrastructure.persistence;

import com.cscreativ.billboard.platformsettings.domain.ConfigType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_settings")
public class PlatformSettingEntity {
    @Id
    @Column(name = "setting_key", nullable = false, unique = true)
    private String key;

    @Column(name = "setting_value", nullable = false, length = 2000)
    private String value;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigType type;

    private LocalDateTime updatedAt;

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ConfigType getType() { return type; }
    public void setType(ConfigType type) { this.type = type; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
