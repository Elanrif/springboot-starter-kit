package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.auth.*;
import com.elanrif.springbootstarterkit.dto.user.UserDto;
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
    public UserDto updateMe(@Valid @RequestBody ProfileDto dto) {
        return authService.update(dto);
    }

    @PatchMapping("/change-password-profile")
    public UserDto changePasswordProfile(@Valid @RequestBody ChangePasswordProfileDto dto) {
        return authService.changePasswordProfile(dto);
    }

    @PatchMapping("/reset-password")
    public UserDto changePassword(@Valid @RequestBody ResetPasswordDto dto) {
        return authService.resetPassword(dto);
    }
}
