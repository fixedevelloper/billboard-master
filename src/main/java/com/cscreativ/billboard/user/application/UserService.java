package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.exception.UserNotFoundException;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.FullName;
import com.cscreativ.billboard.user.domain.valueobject.PhoneNumber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'id : " + id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateProfile(UUID id, String fullNameStr, String phoneNumberStr) {
        User user = getUserById(id);

        String[] parts = fullNameStr.trim().split("\\s+", 2);
        FullName fullName = new FullName(parts[0], parts.length > 1 ? parts[1] : parts[0]);
        PhoneNumber phoneNumber = (phoneNumberStr != null && !phoneNumberStr.isBlank())
                ? new PhoneNumber(phoneNumberStr)
                : null;

        user.updateProfile(fullName, phoneNumber);
        return userRepository.save(user);
    }
}
