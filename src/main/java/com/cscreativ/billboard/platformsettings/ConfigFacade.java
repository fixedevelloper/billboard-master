package com.cscreativ.billboard.platformsettings;

import java.util.Optional;

public interface ConfigFacade {
    Optional<String> getSettingValue(String key);
    String getSettingValueOrDefault(String key, String defaultValue);
    int getIntSetting(String key, int defaultValue);
}
