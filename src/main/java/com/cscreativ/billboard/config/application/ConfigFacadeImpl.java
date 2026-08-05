package com.cscreativ.billboard.config.application;

import com.cscreativ.billboard.config.ConfigFacade;
import com.cscreativ.billboard.config.domain.repository.PlatformSettingRepository;
import com.cscreativ.billboard.config.domain.valueobject.ConfigKey;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConfigFacadeImpl implements ConfigFacade {

    private final PlatformSettingRepository settingRepository;

    public ConfigFacadeImpl(PlatformSettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    @Override
    public Optional<String> getSettingValue(String key) {
        return settingRepository.findByKey(new ConfigKey(key)).map(s -> s.getValue());
    }

    @Override
    public String getSettingValueOrDefault(String key, String defaultValue) {
        return getSettingValue(key).orElse(defaultValue);
    }

    @Override
    public int getIntSetting(String key, int defaultValue) {
        return getSettingValue(key).map(Integer::parseInt).orElse(defaultValue);
    }
}
