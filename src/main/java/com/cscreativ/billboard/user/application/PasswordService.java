package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.exception.UserNotFoundException;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.specification.PasswordPolicy;
import com.cscreativ.billboard.user.domain.valueobject.Password;
import com.cscreativ.billboard.user.events.PasswordChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public PasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    public void changePassword(UUID userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        PasswordPolicy.validateRawPassword(newPassword);
        user.changePassword(new Password(passwordEncoder.encode(newPassword)));
        userRepository.save(user);

        eventPublisher.publishEvent(new PasswordChangedEvent(userId, LocalDateTime.now()));
    }
}
