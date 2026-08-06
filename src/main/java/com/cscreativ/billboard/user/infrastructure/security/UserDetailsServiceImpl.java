package com.cscreativ.billboard.user.infrastructure.security;

import com.cscreativ.billboard.user.domain.Role;
import com.cscreativ.billboard.user.domain.UserStatus;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.cscreativ.billboard.user.domain.User domainUser = userRepository.findByEmail(new Email(username))
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé: " + username));

        return new User(
                domainUser.getEmail().getValue(),
                domainUser.getPassword().getHashedValue(),
                domainUser.getStatus() == UserStatus.ACTIVE,
                true,
                true,
                domainUser.getStatus() != UserStatus.SUSPENDED,
                toAuthorities(domainUser.getRoles())
        );
    }

    private Set<GrantedAuthority> toAuthorities(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> Stream.concat(
                        Stream.of(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase())),
                        role.getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getName()))
                ))
                .collect(Collectors.toSet());
    }
}
