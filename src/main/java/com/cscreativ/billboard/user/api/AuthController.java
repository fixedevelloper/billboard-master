package com.cscreativ.billboard.user.api;

import com.cscreativ.billboard.user.api.mapper.UserMapper;
import com.cscreativ.billboard.user.api.request.LoginRequest;
import com.cscreativ.billboard.user.api.request.RegisterRequest;
import com.cscreativ.billboard.user.api.response.LoginResponse;
import com.cscreativ.billboard.user.api.response.UserResponse;
import com.cscreativ.billboard.user.application.AuthenticationService;
import com.cscreativ.billboard.user.application.RegistrationService;
import com.cscreativ.billboard.user.domain.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;

    public AuthController(RegistrationService registrationService, AuthenticationService authenticationService, UserMapper userMapper) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = registrationService.registerUser(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber()
        );
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authenticationService.login(request.email(), request.password());
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
