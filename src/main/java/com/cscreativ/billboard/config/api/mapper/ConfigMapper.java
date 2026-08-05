package com.cscreativ.billboard.config.api.mapper;

import com.cscreativ.billboard.config.api.response.SettingResponse;
import com.cscreativ.billboard.config.domain.PlatformSetting;
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
