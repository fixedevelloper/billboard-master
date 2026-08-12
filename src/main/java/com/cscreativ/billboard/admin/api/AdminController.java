package com.cscreativ.billboard.admin.api;

import com.cscreativ.billboard.admin.api.mapper.AdminMapper;
import com.cscreativ.billboard.admin.api.request.CreateAdminRequest;
import com.cscreativ.billboard.admin.api.request.LogActionRequest;
import com.cscreativ.billboard.admin.api.response.AdminResponse;
import com.cscreativ.billboard.admin.api.response.AuditLogResponse;
import com.cscreativ.billboard.admin.application.AdminService;
import com.cscreativ.billboard.admin.domain.AdminUser;
import com.cscreativ.billboard.admin.domain.AuditLog;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final AdminService adminService;
    private final AdminMapper adminMapper;

    public AdminController(AdminService adminService, AdminMapper adminMapper) {
        this.adminService = adminService;
        this.adminMapper = adminMapper;
    }

    /**
     * Seul un SUPER_ADMIN peut créer d'autres comptes admin (et donc leur attribuer n'importe
     * quel rôle, y compris SUPER_ADMIN) : sans cette restriction, un admin MODERATOR/SUPPORT/
     * FINANCE pourrait s'auto-élever ou créer des comptes à privilèges illimités.
     */
    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@RequestBody CreateAdminRequest request,
                                                      @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdminRole("SUPER_ADMIN");
        AdminUser admin = adminService.createAdmin(request.userId(), request.roles());
        return ResponseEntity.ok(adminMapper.toResponse(admin));
    }

    @PostMapping("/{id}/actions")
    public ResponseEntity<Void> logAction(@PathVariable UUID id, @RequestBody LogActionRequest request,
                                           @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isAdmin() && id.equals(currentUser.getAdminId()));
        adminService.logAdminAction(id, request.action(), request.targetEntity(), request.targetId(), request.details());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(@PathVariable UUID id,
                                                                @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<AuditLog> logs = adminService.getAuditLogsByAdmin(id);
        return ResponseEntity.ok(logs.stream().map(adminMapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<AuditLog> logs = adminService.getAllAuditLogs();
        return ResponseEntity.ok(logs.stream().map(adminMapper::toResponse).collect(Collectors.toList()));
    }
}
