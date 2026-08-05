package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.exception.EmailAlreadyExistsException;
import com.cscreativ.billboard.user.domain.repository.RoleRepository;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.specification.PasswordPolicy;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.domain.valueobject.FullName;
import com.cscreativ.billboard.user.domain.valueobject.Password;
import com.cscreativ.billboard.user.domain.valueobject.PhoneNumber;
import com.cscreativ.billboard.user.events.UserRegisteredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public RegistrationService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public User registerUser(String emailStr, String rawPassword, String firstName, String lastName, String phoneStr) {
        Email email = new Email(emailStr);
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(emailStr);
        }

        PasswordPolicy.validateRawPassword(rawPassword);
        Password password = new Password(passwordEncoder.encode(rawPassword));
        FullName fullName = new FullName(firstName, lastName);
        PhoneNumber phoneNumber = phoneStr != null ? new PhoneNumber(phoneStr) : null;

        User user = User.create(email, password, fullName, phoneNumber, Set.of());
        User savedUser = userRepository.save(user);

        eventPublisher.publishEvent(new UserRegisteredEvent(
                savedUser.getId(),
                savedUser.getEmail().getValue(),
                savedUser.getFullName().getFirstName(),
                savedUser.getFullName().getLastName(),
                LocalDateTime.now()
        ));

        return savedUser;
    }
}
