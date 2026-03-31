package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PatchMapping("/reset-password")
    public ResponseEntity<UserDto.Response> changePassword(@Valid @RequestBody AuthDto.ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }
}
