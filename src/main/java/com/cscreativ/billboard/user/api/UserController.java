package com.cscreativ.billboard.user.api;

import com.cscreativ.billboard.user.api.mapper.UserMapper;
import com.cscreativ.billboard.user.api.request.ChangePasswordRequest;
import com.cscreativ.billboard.user.api.request.UpdateProfileRequest;
import com.cscreativ.billboard.user.api.response.AdminUserResponse;
import com.cscreativ.billboard.user.api.response.ProfileResponse;
import com.cscreativ.billboard.shared.AuthenticatedUser;
import com.cscreativ.billboard.user.application.PasswordService;
import com.cscreativ.billboard.user.application.UserService;
import com.cscreativ.billboard.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordService passwordService;

    public UserController(UserService userService, UserMapper userMapper, PasswordService passwordService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.passwordService = passwordService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable UUID id, @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(id);
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toProfileResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<AdminUserResponse>> getAllUsers(@AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireAdmin();
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(userMapper::toAdminUserResponse).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable UUID id, @RequestBody UpdateProfileRequest request,
                                                          @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.requireSelfOrAdmin(id);
        User user = userService.updateProfile(id, request.fullName(), request.phoneNumber());
        return ResponseEntity.ok(userMapper.toProfileResponse(user));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable UUID id, @RequestBody ChangePasswordRequest request,
                                                @AuthenticationPrincipal AuthenticatedUser currentUser) {
        currentUser.require(currentUser.isSelf(id));
        passwordService.changePassword(id, request.oldPassword(), request.newPassword());
        return ResponseEntity.noContent().build();
    }
}
