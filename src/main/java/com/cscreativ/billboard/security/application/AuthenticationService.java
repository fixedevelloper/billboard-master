package com.cscreativ.billboard.security.application;

import com.cscreativ.billboard.security.domain.UserCredentials;
import com.cscreativ.billboard.security.domain.UserRole;
import com.cscreativ.billboard.security.domain.exception.InvalidCredentialsException;
import com.cscreativ.billboard.security.domain.repository.UserCredentialsRepository;
import com.cscreativ.billboard.security.domain.valueobject.AccessToken;
import com.cscreativ.billboard.security.events.UserAuthenticatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public AuthenticationService(UserCredentialsRepository credentialsRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public UserCredentials registerCredentials(UUID userId, String email, String rawPassword, Set<UserRole> roles) {
        String passwordHash = passwordEncoder.encode(rawPassword);
        UserCredentials credentials = UserCredentials.create(userId, email, passwordHash, roles);
        return credentialsRepository.save(credentials);
    }

    @Transactional
    public AccessToken authenticate(String email, String rawPassword) {
        UserCredentials credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Identifiants invalides"));

        if (!credentials.isEnabled() || !passwordEncoder.matches(rawPassword, credentials.getPasswordHash())) {
            throw new InvalidCredentialsException("Identifiants invalides ou compte désactivé");
        }

        eventPublisher.publishEvent(new UserAuthenticatedEvent(credentials.getUserId(), credentials.getEmail(), LocalDateTime.now()));

        // Simulation de la génération d'un jeton JWT pour la réponse
        String tokenStr = "jwt-token-sample-" + UUID.randomUUID();
        return new AccessToken(tokenStr, LocalDateTime.now().plusHours(24));
    }
}
