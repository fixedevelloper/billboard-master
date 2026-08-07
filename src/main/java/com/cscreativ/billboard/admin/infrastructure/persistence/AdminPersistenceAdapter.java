package com.cscreativ.billboard.admin.infrastructure.persistence;

import com.cscreativ.billboard.admin.domain.AdminUser;
import com.cscreativ.billboard.admin.domain.AuditLog;
import com.cscreativ.billboard.admin.domain.repository.AdminRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AdminPersistenceAdapter implements AdminRepository {

    private final AdminUserJpaRepository adminUserJpaRepository;
    private final AuditLogJpaRepository auditLogJpaRepository;

    public AdminPersistenceAdapter(AdminUserJpaRepository adminUserJpaRepository, AuditLogJpaRepository auditLogJpaRepository) {
        this.adminUserJpaRepository = adminUserJpaRepository;
        this.auditLogJpaRepository = auditLogJpaRepository;
    }

    @Override
    public AdminUser saveAdmin(AdminUser adminUser) {
        AdminUserEntity entity = toEntity(adminUser);
        AdminUserEntity saved = adminUserJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AdminUser> findAdminById(UUID id) {
        return adminUserJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<AdminUser> findAdminByUserId(UUID userId) {
        return adminUserJpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public AuditLog saveAuditLog(AuditLog auditLog) {
        AuditLogEntity entity = toEntity(auditLog);
        AuditLogEntity saved = auditLogJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AuditLog> findAuditLogsByAdmin(UUID adminId) {
        return auditLogJpaRepository.findByAdminId(adminId).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AuditLog> findAllAuditLogs() {
        return auditLogJpaRepository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    private AdminUserEntity toEntity(AdminUser domain) {
        AdminUserEntity entity = new AdminUserEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setRoles(domain.getRoles());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private AdminUser toDomain(AdminUserEntity entity) {
        return new AdminUser(
                entity.getId(),
                entity.getUserId(),
                entity.getRoles(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private AuditLogEntity toEntity(AuditLog domain) {
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(domain.getId());
        entity.setAdminId(domain.getAdminId());
        entity.setAction(domain.getAction());
        entity.setTargetEntity(domain.getTargetEntity());
        entity.setTargetId(domain.getTargetId());
        entity.setDetails(domain.getDetails());
        entity.setTimestamp(domain.getTimestamp());
        return entity;
    }

    private AuditLog toDomain(AuditLogEntity entity) {
        return new AuditLog(
                entity.getId(),
                entity.getAdminId(),
                entity.getAction(),
                entity.getTargetEntity(),
                entity.getTargetId(),
                entity.getDetails(),
                entity.getTimestamp()
        );
    }
}
