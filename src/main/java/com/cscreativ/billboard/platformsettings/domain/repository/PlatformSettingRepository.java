package com.cscreativ.billboard.platformsettings.domain.repository;

import com.cscreativ.billboard.platformsettings.domain.PlatformSetting;
import com.cscreativ.billboard.platformsettings.domain.valueobject.ConfigKey;

import java.util.List;
import java.util.Optional;

public interface PlatformSettingRepository {
    PlatformSetting save(PlatformSetting setting);
    Optional<PlatformSetting> findByKey(ConfigKey key);
    List<PlatformSetting> findAll();
    void deleteByKey(ConfigKey key);
}
