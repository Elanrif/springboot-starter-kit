package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.services.KeycloakService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/keycloak")
@RequiredArgsConstructor
public class KeycloakController {

    private final KeycloakService keycloakService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.AuthResponse> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(keycloakService.login(request.email(), request.password()));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.AuthResponse> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(keycloakService.createUser(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthDto.TokenResponse> refreshToken(@Valid @RequestBody AuthDto.RefreshTokenRequest request) {
        return ResponseEntity.ok(keycloakService.refreshToken(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody AuthDto.RefreshTokenRequest request) {
        keycloakService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }
}
