package com.cscreativ.billboard.user.application;

import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.exception.UserNotFoundException;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

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
}
