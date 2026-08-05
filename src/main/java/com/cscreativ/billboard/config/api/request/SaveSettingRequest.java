package com.cscreativ.billboard.config.api.request;

import com.cscreativ.billboard.config.domain.ConfigType;

public record SaveSettingRequest(
        String key,
        String value,
        String description,
        ConfigType type
) {}
