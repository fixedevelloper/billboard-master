package com.cscreativ.billboard.user.domain;

import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.domain.valueobject.FullName;
import com.cscreativ.billboard.user.domain.valueobject.Password;
import com.cscreativ.billboard.user.domain.valueobject.PhoneNumber;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class User {
    private UUID id;
    private Email email;
    private Password password;
    private FullName fullName;
    private PhoneNumber phoneNumber;
    private UserStatus status;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UUID id, Email email, Password password, FullName fullName, PhoneNumber phoneNumber, UserStatus status, Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(Email email, Password password, FullName fullName, PhoneNumber phoneNumber, Set<Role> roles) {
        LocalDateTime now = LocalDateTime.now();
        return new User(UUID.randomUUID(), email, password, fullName, phoneNumber, UserStatus.PENDING_VERIFICATION, roles, now, now);
    }

    public void verify() {
        this.status = UserStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
        this.updatedAt = LocalDateTime.now();
    }

    public void changePassword(Password newPassword) {
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public Email getEmail() { return email; }
    public Password getPassword() { return password; }
    public FullName getFullName() { return fullName; }
    public PhoneNumber getPhoneNumber() { return phoneNumber; }
    public UserStatus getStatus() { return status; }
    public Set<Role> getRoles() { return roles; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
