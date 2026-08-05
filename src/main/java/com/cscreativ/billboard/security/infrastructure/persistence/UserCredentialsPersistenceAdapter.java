package com.cscreativ.billboard.security.infrastructure.persistence;

import com.cscreativ.billboard.security.domain.UserCredentials;
import com.cscreativ.billboard.security.domain.repository.UserCredentialsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserCredentialsPersistenceAdapter implements UserCredentialsRepository {

    private final UserCredentialsJpaRepository jpaRepository;

    public UserCredentialsPersistenceAdapter(UserCredentialsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserCredentials save(UserCredentials credentials) {
        UserCredentialsEntity entity = toEntity(credentials);
        UserCredentialsEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<UserCredentials> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<UserCredentials> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<UserCredentials> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    private UserCredentialsEntity toEntity(UserCredentials domain) {
        UserCredentialsEntity entity = new UserCredentialsEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setRoles(domain.getRoles());
        entity.setEnabled(domain.isEnabled());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private UserCredentials toDomain(UserCredentialsEntity entity) {
        return new UserCredentials(
                entity.getId(),
                entity.getUserId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRoles(),
                entity.isEnabled(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
