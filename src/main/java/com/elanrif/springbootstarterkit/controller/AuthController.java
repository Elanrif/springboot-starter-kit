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

    // =========================================================
    // Authentication
    // =========================================================

    @PostMapping("/login")
    public ResponseEntity<AuthDto.Response> login(
            @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        log.info(
                "POST /api/v1/auth/login - Login attempt for email: {}",
                request.email()
        );

        AuthDto.Response response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.Response> register(
            @Valid @RequestBody AuthDto.RegisterRequest request
    ) {
        log.info(
                "POST /api/v1/auth/register - Registration attempt for email: {}",
                request.email()
        );

        AuthDto.Response response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto.Response> updateMyProfile(
            @Valid @RequestBody AuthDto.ProfileUpdateRequest request
    ) {
        log.info(
                "PATCH /api/v1/auth/me - Updating profile for email: {}",
                request.email()
        );

        UserDto.Response response =
                authService.updateMyProfile(request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> changeMyPassword(
            @Valid @RequestBody AuthDto.ChangePasswordRequest request
    ) {
        log.info(
                "PATCH /api/v1/auth/me/password - Changing password for email: {}",
                request.email()
        );

        authService.changeMyPassword(request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password/reset")
    public ResponseEntity<Void> resetMyPassword(
            @Valid @RequestBody AuthDto.ResetPasswordRequest request
    ) {
        log.info(
                "PATCH /api/v1/auth/password/reset - Resetting password for email: {}",
                request.email()
        );

        authService.resetMyPassword(request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMyAccount(
            @Valid @RequestBody AuthDto.DeleteAccountRequest request
    ) {
        log.info(
                "DELETE /api/v1/auth/me - Deleting account"
        );

        authService.deleteMyAccount(request);

        return ResponseEntity.noContent().build();
    }
}