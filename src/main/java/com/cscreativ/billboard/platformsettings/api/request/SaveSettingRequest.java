package com.cscreativ.billboard.platformsettings.api.request;

import com.cscreativ.billboard.platformsettings.domain.ConfigType;

public record SaveSettingRequest(
        String key,
        String value,
        String description,
        ConfigType type
) {}
