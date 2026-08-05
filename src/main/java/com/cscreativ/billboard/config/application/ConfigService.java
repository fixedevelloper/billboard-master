package com.cscreativ.billboard.config.application;

import com.cscreativ.billboard.config.domain.ConfigType;
import com.cscreativ.billboard.config.domain.PlatformSetting;
import com.cscreativ.billboard.config.domain.exception.SettingNotFoundException;
import com.cscreativ.billboard.config.domain.repository.PlatformSettingRepository;
import com.cscreativ.billboard.config.domain.valueobject.ConfigKey;
import com.cscreativ.billboard.config.events.SettingUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConfigService {

    private final PlatformSettingRepository settingRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ConfigService(PlatformSettingRepository settingRepository, ApplicationEventPublisher eventPublisher) {
        this.settingRepository = settingRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PlatformSetting setSetting(String keyStr, String value, String description, ConfigType type) {
        ConfigKey key = new ConfigKey(keyStr);
        Optional<PlatformSetting> existing = settingRepository.findByKey(key);

        PlatformSetting setting;
        if (existing.isPresent()) {
            setting = existing.get();
            String oldValue = setting.getValue();
            setting.updateValue(value);
            settingRepository.save(setting);
            eventPublisher.publishEvent(new SettingUpdatedEvent(key.getValue(), oldValue, value, LocalDateTime.now()));
        } else {
            setting = PlatformSetting.create(key, value, description, type);
            settingRepository.save(setting);
        }
        return setting;
    }

    public PlatformSetting getSetting(String keyStr) {
        ConfigKey key = new ConfigKey(keyStr);
        return settingRepository.findByKey(key)
                .orElseThrow(() -> new SettingNotFoundException("Paramètre de configuration introuvable : " + keyStr));
    }

    public List<PlatformSetting> getAllSettings() {
        return settingRepository.findAll();
    }
}
