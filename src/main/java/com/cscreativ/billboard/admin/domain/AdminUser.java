package com.cscreativ.billboard.admin.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdminUser {
    private UUID id;
    private UUID userId;
    private Set<AdminRole> roles;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AdminUser(UUID id, UUID userId, Set<AdminRole> roles, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.roles = roles != null ? roles : new HashSet<>();
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AdminUser create(UUID userId, Set<AdminRole> roles) {
        LocalDateTime now = LocalDateTime.now();
        return new AdminUser(UUID.randomUUID(), userId, roles, true, now, now);
    }

    public void addRole(AdminRole role) {
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Set<AdminRole> getRoles() { return roles; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
