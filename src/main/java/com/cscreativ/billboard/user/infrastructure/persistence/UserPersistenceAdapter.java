package com.cscreativ.billboard.user.infrastructure.persistence;

import com.cscreativ.billboard.user.domain.Permission;
import com.cscreativ.billboard.user.domain.Role;
import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.domain.repository.UserRepository;
import com.cscreativ.billboard.user.domain.valueobject.Email;
import com.cscreativ.billboard.user.domain.valueobject.FullName;
import com.cscreativ.billboard.user.domain.valueobject.Password;
import com.cscreativ.billboard.user.domain.valueobject.PhoneNumber;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity saved = userJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return userJpaRepository.findByEmail(email.getValue()).map(this::toDomain);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userJpaRepository.existsByEmail(email.getValue());
    }

    private UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail().getValue());
        entity.setPassword(domain.getPassword().getHashedValue());
        entity.setFirstName(domain.getFullName().getFirstName());
        entity.setLastName(domain.getFullName().getLastName());
        entity.setPhoneNumber(domain.getPhoneNumber() != null ? domain.getPhoneNumber().getValue() : null);
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                new Email(entity.getEmail()),
                new Password(entity.getPassword()),
                new FullName(entity.getFirstName(), entity.getLastName()),
                entity.getPhoneNumber() != null ? new PhoneNumber(entity.getPhoneNumber()) : null,
                entity.getStatus(),
                entity.getRoles() != null ? entity.getRoles().stream().map(this::toRoleDomain).collect(Collectors.toSet()) : Set.of(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private Role toRoleDomain(RoleEntity entity) {
        return new Role(
                entity.getId(),
                entity.getName(),
                entity.getPermissions() != null ? entity.getPermissions().stream()
                        .map(p -> new Permission(p.getId(), p.getName())).collect(Collectors.toSet()) : Set.of()
        );
    }
}
