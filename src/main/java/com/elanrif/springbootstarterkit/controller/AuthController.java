package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthDto.Response> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        log.info("POST /api/v1/auth/login - Login attempt for email: {}", request.email());
        AuthDto.Response response = authService.login(request);
        log.info("Login successful for email: {}", request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.Response> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        log.info("POST /api/v1/auth/register - Registration attempt for email: {}", request.email());
        AuthDto.Response response = authService.register(request);
        log.info("Registration successful for email: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/edit-profile")
    public ResponseEntity<UserDto.Response> updateMe(@Valid @RequestBody AuthDto.ProfileUpdateRequest request) {
        log.info("PATCH /api/v1/auth/edit-profile - Updating profile for email: {}", request.email());
        UserDto.Response response = authService.update(request);
        log.info("Profile updated successfully for email: {}", request.email());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password-profile")
    public ResponseEntity<UserDto.Response> changePasswordProfile(@Valid @RequestBody AuthDto.ChangePasswordRequest request) {
        log.info("PATCH /api/v1/auth/change-password-profile - Changing password for email: {}", request.email());
        UserDto.Response response = authService.changePasswordProfile(request);
        log.info("Password changed successfully for email: {}", request.email());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<UserDto.Response> changePassword(@Valid @RequestBody AuthDto.ResetPasswordRequest request) {
        log.info("PATCH /api/v1/auth/reset-password - Resetting password for email: {}", request.email());
        UserDto.Response response = authService.resetPassword(request);
        log.info("Password reset successfully for email: {}", request.email());
        return ResponseEntity.ok(response);
    }
}
