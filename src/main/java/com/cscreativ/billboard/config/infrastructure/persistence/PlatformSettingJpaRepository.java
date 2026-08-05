package com.cscreativ.billboard.config.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformSettingJpaRepository extends JpaRepository<PlatformSettingEntity, String> {
}
