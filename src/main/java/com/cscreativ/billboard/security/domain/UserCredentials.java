package com.cscreativ.billboard.security.domain;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class UserCredentials {
    private UUID id;
    private UUID userId;
    private String email;
    private String passwordHash;
    private Set<UserRole> roles;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserCredentials(UUID id, UUID userId, String email, String passwordHash, Set<UserRole> roles, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserCredentials create(UUID userId, String email, String passwordHash, Set<UserRole> roles) {
        LocalDateTime now = LocalDateTime.now();
        return new UserCredentials(UUID.randomUUID(), userId, email, passwordHash, roles, true, now, now);
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = newPasswordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Set<UserRole> getRoles() { return roles; }
    public boolean isEnabled() { return enabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
