package com.cscreativ.billboard.config.infrastructure.persistence;

import com.cscreativ.billboard.config.domain.PlatformSetting;
import com.cscreativ.billboard.config.domain.repository.PlatformSettingRepository;
import com.cscreativ.billboard.config.domain.valueobject.ConfigKey;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PlatformSettingPersistenceAdapter implements PlatformSettingRepository {

    private final PlatformSettingJpaRepository jpaRepository;

    public PlatformSettingPersistenceAdapter(PlatformSettingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PlatformSetting save(PlatformSetting setting) {
        PlatformSettingEntity entity = toEntity(setting);
        PlatformSettingEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PlatformSetting> findByKey(ConfigKey key) {
        return jpaRepository.findById(key.getValue()).map(this::toDomain);
    }

    @Override
    public List<PlatformSetting> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteByKey(ConfigKey key) {
        jpaRepository.deleteById(key.getValue());
    }

    private PlatformSettingEntity toEntity(PlatformSetting domain) {
        PlatformSettingEntity entity = new PlatformSettingEntity();
        entity.setKey(domain.getKey().getValue());
        entity.setValue(domain.getValue());
        entity.setDescription(domain.getDescription());
        entity.setType(domain.getType());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private PlatformSetting toDomain(PlatformSettingEntity entity) {
        return new PlatformSetting(
                new ConfigKey(entity.getKey()),
                entity.getValue(),
                entity.getDescription(),
                entity.getType(),
                entity.getUpdatedAt()
        );
    }
}
