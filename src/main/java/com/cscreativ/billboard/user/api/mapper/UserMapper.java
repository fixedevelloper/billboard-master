package com.cscreativ.billboard.user.api.mapper;

import com.cscreativ.billboard.user.api.response.AdminUserResponse;
import com.cscreativ.billboard.user.api.response.ProfileResponse;
import com.cscreativ.billboard.user.api.response.UserResponse;
import com.cscreativ.billboard.user.domain.Role;
import com.cscreativ.billboard.user.domain.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail().getValue(),
                user.getFullName().getFirstName(),
                user.getFullName().getLastName(),
                user.getStatus().name()
        );
    }

    public ProfileResponse toProfileResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getEmail().getValue(),
                user.getFullName().getFormattedName(),
                user.getPhoneNumber() != null ? user.getPhoneNumber().getValue() : null,
                user.getStatus().name()
        );
    }

    public AdminUserResponse toAdminUserResponse(User user) {
        List<String> roles = user.getRoles() != null
                ? user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                : List.of();
        return new AdminUserResponse(
                user.getId(),
                user.getEmail().getValue(),
                user.getFullName().getFormattedName(),
                user.getPhoneNumber() != null ? user.getPhoneNumber().getValue() : null,
                user.getStatus().name(),
                roles,
                user.getCreatedAt()
        );
    }
}
