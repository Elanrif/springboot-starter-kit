package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PatchMapping("/edit-profile")
    public ResponseEntity<UserDto.Response> updateMe(@Valid @RequestBody AuthDto.ProfileUpdateRequest request) {
        return ResponseEntity.ok(authService.update(request));
    }

    @PatchMapping("/change-password-profile")
    public ResponseEntity<UserDto.Response> changePasswordProfile(@Valid @RequestBody AuthDto.ChangePasswordRequest request) {
        return ResponseEntity.ok(authService.changePasswordProfile(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.Response> login(@Valid @RequestBody AuthDto.LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.Response> register(@Valid @RequestBody AuthDto.RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<UserDto.Response> changePassword(@Valid @RequestBody AuthDto.ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
