package com.cscreativ.billboard.platformsettings.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformSettingJpaRepository extends JpaRepository<PlatformSettingEntity, String> {
}
