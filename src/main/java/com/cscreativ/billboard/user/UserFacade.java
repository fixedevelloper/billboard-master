package com.cscreativ.billboard.user;

import com.cscreativ.billboard.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserFacade {
    Optional<User> findUserById(UUID userId);
    boolean existsByEmail(String email);
}
