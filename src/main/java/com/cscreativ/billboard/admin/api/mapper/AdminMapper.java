package com.cscreativ.billboard.admin.api.mapper;

import com.cscreativ.billboard.admin.api.response.AdminResponse;
import com.cscreativ.billboard.admin.api.response.AuditLogResponse;
import com.cscreativ.billboard.admin.domain.AdminUser;
import com.cscreativ.billboard.admin.domain.AuditLog;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class AdminMapper {

    public AdminResponse toResponse(AdminUser admin) {
        return new AdminResponse(
                admin.getId(),
                admin.getUserId(),
                admin.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                admin.isActive()
        );
    }

    public AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getAdminId(),
                log.getAction().name(),
                log.getTargetEntity(),
                log.getTargetId(),
                log.getDetails(),
                log.getTimestamp()
        );
    }
}
