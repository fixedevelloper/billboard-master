package com.cscreativ.billboard.user.domain;

import com.cscreativ.billboard.user.domain.exception.InvalidVerificationTokenException;
import com.cscreativ.billboard.user.domain.exception.VerificationTokenExpiredException;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.domain.valueobject.FullName;
import com.cscreativ.billboard.user.domain.valueobject.Password;
import com.cscreativ.billboard.user.domain.valueobject.PhoneNumber;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class User {

    private static final SecureRandom TOKEN_RANDOM = new SecureRandom();
    private static final Duration VERIFICATION_TOKEN_TTL = Duration.ofHours(24);

    private UUID id;
    private Email email;
    private Password password;
    private FullName fullName;
    private PhoneNumber phoneNumber;
    private UserStatus status;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String verificationToken;
    private LocalDateTime verificationTokenExpiresAt;

    public User(UUID id, Email email, Password password, FullName fullName, PhoneNumber phoneNumber, UserStatus status, Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt, String verificationToken, LocalDateTime verificationTokenExpiresAt) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.verificationToken = verificationToken;
        this.verificationTokenExpiresAt = verificationTokenExpiresAt;
    }

    public static User create(Email email, Password password, FullName fullName, PhoneNumber phoneNumber, Set<Role> roles) {
        LocalDateTime now = LocalDateTime.now();
        User user = new User(UUID.randomUUID(), email, password, fullName, phoneNumber, UserStatus.PENDING_VERIFICATION, roles, now, now, null, null);
        user.issueVerificationToken();
        return user;
    }

    /** Régénère un code de vérification à 6 chiffres, valable 24h (nouvelle inscription ou renvoi). */
    public String issueVerificationToken() {
        this.verificationToken = String.format("%06d", TOKEN_RANDOM.nextInt(1_000_000));
        this.verificationTokenExpiresAt = LocalDateTime.now().plus(VERIFICATION_TOKEN_TTL);
        this.updatedAt = LocalDateTime.now();
        return this.verificationToken;
    }

    /** Vérifie le compte à partir du code reçu par e-mail. Idempotent si déjà actif. */
    public void confirmVerification(String candidateToken) {
        if (status == UserStatus.ACTIVE) {
            return;
        }
        if (verificationToken == null || !verificationToken.equals(candidateToken)) {
            throw new InvalidVerificationTokenException();
        }
        if (verificationTokenExpiresAt == null || verificationTokenExpiresAt.isBefore(LocalDateTime.now())) {
            throw new VerificationTokenExpiredException();
        }
        this.status = UserStatus.ACTIVE;
        this.verificationToken = null;
        this.verificationTokenExpiresAt = null;
        this.updatedAt = LocalDateTime.now();
    }

    /** Active directement le compte sans code, réservé au bootstrap du compte admin. */
    public void verify() {
        this.status = UserStatus.ACTIVE;
        this.verificationToken = null;
        this.verificationTokenExpiresAt = null;
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

    public void updateProfile(FullName fullName, PhoneNumber phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
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
    public String getVerificationToken() { return verificationToken; }
    public LocalDateTime getVerificationTokenExpiresAt() { return verificationTokenExpiresAt; }
}
