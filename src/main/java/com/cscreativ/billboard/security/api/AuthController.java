package com.cscreativ.billboard.security.api;

import com.cscreativ.billboard.security.api.request.LoginRequest;
import com.cscreativ.billboard.security.api.request.RegisterCredentialsRequest;
import com.cscreativ.billboard.security.api.response.AuthenticationResponse;
import com.cscreativ.billboard.security.application.AuthenticationService;
import com.cscreativ.billboard.security.domain.UserCredentials;
import com.cscreativ.billboard.security.domain.valueobject.AccessToken;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterCredentialsRequest request) {
        authenticationService.registerCredentials(
                request.userId(),
                request.email(),
                request.password(),
                request.roles()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        AccessToken token = authenticationService.authenticate(request.email(), request.password());
        return ResponseEntity.ok(new AuthenticationResponse(token.getToken(), token.getExpiresAt()));
    }
}
