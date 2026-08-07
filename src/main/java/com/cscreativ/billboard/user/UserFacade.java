package com.cscreativ.billboard.user;

import com.cscreativ.billboard.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface UserFacade {
    Optional<User> findUserById(UUID userId);
    boolean existsByEmail(String email);

    /**
     * Crée le compte s'il n'existe pas encore (sinon le récupère), s'assure qu'il est ACTIF
     * (contourne la vérification par e-mail), et retourne son id. Idempotent — utilisé par
     * le bootstrap du compte admin depuis les variables d'environnement.
     */
    UUID ensureActiveUser(String email, String rawPassword, String firstName, String lastName);
}
