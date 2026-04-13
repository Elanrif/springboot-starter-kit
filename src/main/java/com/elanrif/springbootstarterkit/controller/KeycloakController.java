package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.services.KeycloakService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/keycloak")
@RequiredArgsConstructor
public class KeycloakController {

    private final KeycloakService keycloakService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.Response> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        log.info("POST /api/v1/keycloak/login - Login attempt for email: {}", request.email());
        AuthDto.Response response = keycloakService.login(request.email(), request.password());
        log.info("Login successful for email: {}", request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.Response> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        log.info("POST /api/v1/keycloak/register - Registering user with email: {}", request.email());
        AuthDto.Response response = keycloakService.createUser(request);
        log.info("User registered successfully with email: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthDto.TokenResponse> refreshToken(@Valid @RequestBody AuthDto.RefreshTokenRequest request) {
        log.info("POST /api/v1/keycloak/refresh-token - Refreshing token");
        AuthDto.TokenResponse response = keycloakService.refreshToken(request.refreshToken());
        log.info("Token refreshed successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthDto.RefreshTokenRequest request) {
        log.info("POST /api/v1/keycloak/logout - Logging out user");
        keycloakService.logout(request.refreshToken());
        log.info("User logged out successfully");
        return ResponseEntity.noContent().build();
    }
}
