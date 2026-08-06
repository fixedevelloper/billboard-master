package com.cscreativ.billboard.platformsettings.api.mapper;

import com.cscreativ.billboard.platformsettings.api.response.SettingResponse;
import com.cscreativ.billboard.platformsettings.domain.PlatformSetting;
import org.springframework.stereotype.Component;

@Component
public class ConfigMapper {

    public SettingResponse toResponse(PlatformSetting setting) {
        return new SettingResponse(
                setting.getKey().getValue(),
                setting.getValue(),
                setting.getDescription(),
                setting.getType().name(),
                setting.getUpdatedAt()
        );
    }
}
