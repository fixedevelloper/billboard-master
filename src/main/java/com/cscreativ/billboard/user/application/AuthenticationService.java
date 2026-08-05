package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.exception.InvalidPasswordException;
import com.cscreativ.billboard.user.domain.exception.UserNotFoundException;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.events.UserLoggedInEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher eventPublisher;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.eventPublisher = eventPublisher;
    }

    public String login(String emailStr, String rawPassword) {
        User user = userRepository.findByEmail(new Email(emailStr))
                .orElseThrow(() -> new UserNotFoundException("Identifiants invalides"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword().getHashedValue())) {
            throw new InvalidPasswordException("Identifiants invalides");
        }

        eventPublisher.publishEvent(new UserLoggedInEvent(user.getId(), LocalDateTime.now()));
        return jwtService.generateAccessToken(user.getId(), user.getEmail().getValue());
    }
}
