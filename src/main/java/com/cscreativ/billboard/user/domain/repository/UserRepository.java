package com.cscreativ.billboard.user.domain.repository;

import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(Email email);
    boolean existsByEmail(Email email);
}
