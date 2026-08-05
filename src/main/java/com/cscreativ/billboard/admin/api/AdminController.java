package com.cscreativ.billboard.admin.api;

import com.cscreativ.billboard.admin.api.mapper.AdminMapper;
import com.cscreativ.billboard.admin.api.request.CreateAdminRequest;
import com.cscreativ.billboard.admin.api.request.LogActionRequest;
import com.cscreativ.billboard.admin.api.response.AdminResponse;
import com.cscreativ.billboard.admin.api.response.AuditLogResponse;
import com.cscreativ.billboard.admin.application.AdminService;
import com.cscreativ.billboard.admin.domain.AdminUser;
import com.cscreativ.billboard.admin.domain.AuditLog;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<AdminResponse> createAdmin(@RequestBody CreateAdminRequest request) {
        AdminUser admin = adminService.createAdmin(request.userId(), request.roles());
        return ResponseEntity.ok(adminMapper.toResponse(admin));
    }

    @PostMapping("/{id}/actions")
    public ResponseEntity<Void> logAction(@PathVariable UUID id, @RequestBody LogActionRequest request) {
        adminService.logAdminAction(id, request.action(), request.targetEntity(), request.targetId(), request.details());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(@PathVariable UUID id) {
        List<AuditLog> logs = adminService.getAuditLogsByAdmin(id);
        return ResponseEntity.ok(logs.stream().map(adminMapper::toResponse).collect(Collectors.toList()));
    }
}
