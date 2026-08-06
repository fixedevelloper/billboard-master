package com.cscreativ.billboard.platformsettings.events;

import java.time.LocalDateTime;

public record SettingUpdatedEvent(String key, String oldValue, String newValue, LocalDateTime occurredOn) {}
