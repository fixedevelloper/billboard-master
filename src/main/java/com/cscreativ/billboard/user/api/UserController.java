package com.cscreativ.billboard.user.api;

import com.cscreativ.billboard.user.api.mapper.UserMapper;
import com.cscreativ.billboard.user.api.response.ProfileResponse;
import com.cscreativ.billboard.user.application.UserService;
import com.cscreativ.billboard.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toProfileResponse(user));
    }
}
