package com.cscreativ.billboard.admin.application;

import com.cscreativ.billboard.admin.domain.AdminRole;
import com.cscreativ.billboard.admin.domain.AdminUser;
import com.cscreativ.billboard.admin.domain.AuditAction;
import com.cscreativ.billboard.admin.domain.AuditLog;
import com.cscreativ.billboard.admin.domain.exception.AdminNotFoundException;
import com.cscreativ.billboard.admin.domain.repository.AdminRepository;
import com.cscreativ.billboard.admin.events.AdminActionPerformedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AdminService(AdminRepository adminRepository, ApplicationEventPublisher eventPublisher) {
        this.adminRepository = adminRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AdminUser createAdmin(UUID userId, Set<AdminRole> roles) {
        AdminUser admin = AdminUser.create(userId, roles);
        return adminRepository.saveAdmin(admin);
    }

    @Transactional
    public void logAdminAction(UUID adminId, AuditAction action, String targetEntity, UUID targetId, String details) {
        AdminUser admin = adminRepository.findAdminById(adminId)
                .orElseThrow(() -> new AdminNotFoundException("Administrateur non trouvé avec l'id : " + adminId));

        AuditLog log = AuditLog.create(admin.getId(), action, targetEntity, targetId, details);
        adminRepository.saveAuditLog(log);

        eventPublisher.publishEvent(new AdminActionPerformedEvent(admin.getId(), action.name(), targetEntity, targetId, LocalDateTime.now()));
    }

    public List<AuditLog> getAuditLogsByAdmin(UUID adminId) {
        return adminRepository.findAuditLogsByAdmin(adminId);
    }

    public List<AuditLog> getAllAuditLogs() {
        return adminRepository.findAllAuditLogs();
    }
}
