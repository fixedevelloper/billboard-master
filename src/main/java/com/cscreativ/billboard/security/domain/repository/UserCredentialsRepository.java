package com.cscreativ.billboard.security.domain.repository;

import com.cscreativ.billboard.security.domain.UserCredentials;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialsRepository {
    UserCredentials save(UserCredentials credentials);
    Optional<UserCredentials> findById(UUID id);
    Optional<UserCredentials> findByEmail(String email);
    Optional<UserCredentials> findByUserId(UUID userId);
}
