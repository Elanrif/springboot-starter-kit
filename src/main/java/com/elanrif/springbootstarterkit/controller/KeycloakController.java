package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.auth.KeycloakAuthResponse;
import com.elanrif.springbootstarterkit.dto.auth.KeycloakTokenResponse;
import com.elanrif.springbootstarterkit.dto.auth.LoginDto;
import com.elanrif.springbootstarterkit.dto.auth.RefreshTokenDto;
import com.elanrif.springbootstarterkit.dto.auth.RegisterDto;
import com.elanrif.springbootstarterkit.services.KeycloakService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/keycloak")
@RequiredArgsConstructor
public class KeycloakController {

    private final KeycloakService keycloakService;

    @PostMapping("/login")
    public KeycloakAuthResponse login(@Valid @RequestBody LoginDto dto) {
        return keycloakService.login(dto.email(), dto.password());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public KeycloakAuthResponse register(@Valid @RequestBody RegisterDto dto) {
        return keycloakService.createUser(dto);
    }

    @PostMapping("/refresh-token")
    public KeycloakTokenResponse refreshToken(@Valid @RequestBody RefreshTokenDto dto) {
        return keycloakService.refreshToken(dto.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenDto dto) {
        keycloakService.logout(dto.refreshToken());
    }
}
