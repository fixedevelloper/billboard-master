package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.UserFacade;
import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.UserStatus;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserFacadeImpl implements UserFacade {

    private final UserRepository userRepository;
    private final RegistrationService registrationService;

    public UserFacadeImpl(UserRepository userRepository, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
    }

    @Override
    public Optional<User> findUserById(UUID userId) {
        return userRepository.findById(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(new Email(email));
    }

    @Override
    public UUID ensureActiveUser(String email, String rawPassword, String firstName, String lastName) {
        User user = userRepository.findByEmail(new Email(email))
                .orElseGet(() -> registrationService.registerUser(email, rawPassword, firstName, lastName, null));

        if (user.getStatus() != UserStatus.ACTIVE) {
            user.verify();
            user = userRepository.save(user);
        }

        return user.getId();
    }
}
