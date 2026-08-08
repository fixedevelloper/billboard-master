package com.cscreativ.billboard.user.api;

import com.cscreativ.billboard.user.api.mapper.UserMapper;
import com.cscreativ.billboard.user.api.request.LoginRequest;
import com.cscreativ.billboard.user.api.request.RegisterRequest;
import com.cscreativ.billboard.user.api.response.LoginResponse;
import com.cscreativ.billboard.user.api.response.UserResponse;
import com.cscreativ.billboard.user.application.AuthenticationService;
import com.cscreativ.billboard.user.application.RegistrationService;
import com.cscreativ.billboard.user.domain.User;
import com.cscreativ.billboard.user.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;

    public AuthController(RegistrationService registrationService,
                           AuthenticationService authenticationService,
                           UserMapper userMapper,
                           JwtTokenProvider tokenProvider) {
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = registrationService.registerUser(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber()
        );
        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }

    /**
     * Le JWT part uniquement dans un cookie HttpOnly (voir JwtTokenProvider) : il n'est jamais
     * renvoyé dans le corps de la réponse, donc jamais accessible en JS côté frontend (donc pas
     * stockable en localStorage). Le corps de la réponse ne contient que des identifiants non
     * sensibles (userId, email, ids de profil) dont le frontend a besoin pour son routage.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        String token = authenticationService.login(request.email(), request.password());
        response.addHeader(HttpHeaders.SET_COOKIE, tokenProvider.buildAuthCookie(token).toString());
        return ResponseEntity.ok(toLoginResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, tokenProvider.clearAuthCookie().toString());
        return ResponseEntity.noContent().build();
    }

    /**
     * Permet au frontend de retrouver la session en cours (ex. après rechargement de page) sans
     * jamais avoir eu accès au JWT lui-même : il ne peut lire ni le cookie (HttpOnly) ni un
     * quelconque stockage local, donc il interroge le backend pour reconstituer son état.
     */
    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(HttpServletRequest request) {
        String token = tokenProvider.extractTokenFromCookies(request);
        if (token == null || !tokenProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(toLoginResponse(token));
    }

    private LoginResponse toLoginResponse(String token) {
        UUID userId = tokenProvider.getUserIdFromToken(token);
        String email = tokenProvider.getEmailFromToken(token);
        return new LoginResponse(
                userId,
                email,
                tokenProvider.getClaim(token, "advertiserId"),
                tokenProvider.getClaim(token, "ownerId"),
                tokenProvider.getClaim(token, "mediaBuyerId"),
                tokenProvider.getClaim(token, "adminId")
        );
    }
}
