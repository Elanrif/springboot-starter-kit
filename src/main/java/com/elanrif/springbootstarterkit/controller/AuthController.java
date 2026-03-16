package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.auth.*;
import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.elanrif.springbootstarterkit.dto.user.UserUpdateDto;
import com.elanrif.springbootstarterkit.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PatchMapping("/edit-profile")
    public UserDto updateMe(@PathVariable Long id, @Valid @RequestBody ProfileDto dto) {
        return authService.update(id, dto);
    }

    @PatchMapping("/change-password-profile")
    public UserDto changePasswordProfile(@Valid @RequestBody ChangePasswordProfileDto dto) {
        return authService.changePasswordProfile(dto);
    }

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterDto dto) {
        return authService.register(dto);
    }

    @PatchMapping("/reset-password")
    public UserDto changePassword(@Valid @RequestBody ResetPasswordDto dto) {
        return authService.resetPassword(dto);
    }
}
