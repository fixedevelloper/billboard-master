package com.cscreativ.billboard.config.api;

import com.cscreativ.billboard.config.api.mapper.ConfigMapper;
import com.cscreativ.billboard.config.api.request.SaveSettingRequest;
import com.cscreativ.billboard.config.api.response.SettingResponse;
import com.cscreativ.billboard.config.application.ConfigService;
import com.cscreativ.billboard.config.domain.PlatformSetting;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/configs")
public class ConfigController {

    private final ConfigService configService;
    private final ConfigMapper configMapper;

    public ConfigController(ConfigService configService, ConfigMapper configMapper) {
        this.configService = configService;
        this.configMapper = configMapper;
    }

    @PostMapping
    public ResponseEntity<SettingResponse> saveSetting(@RequestBody SaveSettingRequest request) {
        PlatformSetting setting = configService.setSetting(
                request.key(),
                request.value(),
                request.description(),
                request.type()
        );
        return ResponseEntity.ok(configMapper.toResponse(setting));
    }

    @GetMapping("/{key}")
    public ResponseEntity<SettingResponse> getSettingByKey(@PathVariable String key) {
        PlatformSetting setting = configService.getSetting(key);
        return ResponseEntity.ok(configMapper.toResponse(setting));
    }

    @GetMapping
    public ResponseEntity<List<SettingResponse>> getAllSettings() {
        List<PlatformSetting> settings = configService.getAllSettings();
        return ResponseEntity.ok(settings.stream().map(configMapper::toResponse).collect(Collectors.toList()));
    }
}
