package com.cscreativ.billboard.platformsettings.api.response;

import java.time.LocalDateTime;

public record SettingResponse(
        String key,
        String value,
        String description,
        String type,
        LocalDateTime updatedAt
) {}
