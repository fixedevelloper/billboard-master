package com.cscreativ.billboard.admin.domain.repository;

import com.cscreativ.billboard.admin.domain.AdminUser;
import com.cscreativ.billboard.admin.domain.AuditLog;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminRepository {
    AdminUser saveAdmin(AdminUser adminUser);
    Optional<AdminUser> findAdminById(UUID id);
    Optional<AdminUser> findAdminByUserId(UUID userId);
    
    AuditLog saveAuditLog(AuditLog auditLog);
    List<AuditLog> findAuditLogsByAdmin(UUID adminId);
    List<AuditLog> findAllAuditLogs();
}
