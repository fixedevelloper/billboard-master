package com.cscreativ.billboard.admin.application;

import com.cscreativ.billboard.admin.AdminFacade;
import com.cscreativ.billboard.admin.domain.AdminRole;
import com.cscreativ.billboard.admin.domain.AdminUser;
import com.cscreativ.billboard.admin.domain.repository.AdminRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AdminFacadeImpl implements AdminFacade {

    private final AdminRepository adminRepository;
    private final AdminService adminService;

    public AdminFacadeImpl(AdminRepository adminRepository, AdminService adminService) {
        this.adminRepository = adminRepository;
        this.adminService = adminService;
    }

    @Override
    public Optional<AdminUser> findAdminById(UUID adminId) {
        return adminRepository.findAdminById(adminId);
    }

    @Override
    public Optional<UUID> findAdminIdByUserId(UUID userId) {
        return adminRepository.findAdminByUserId(userId).map(AdminUser::getId);
    }

    @Override
    public boolean hasRole(UUID adminId, String roleName) {
        return adminRepository.findAdminById(adminId)
                .map(admin -> admin.getRoles().stream().anyMatch(role -> role.name().equals(roleName)))
                .orElse(false);
    }

    @Override
    public UUID createAdmin(UUID userId, Set<String> roleNames) {
        Set<AdminRole> roles = roleNames.stream().map(AdminRole::valueOf).collect(Collectors.toSet());
        return adminService.createAdmin(userId, roles).getId();
    }
}
