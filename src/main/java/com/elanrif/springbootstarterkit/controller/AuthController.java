package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.auth.ChangePasswordDto;
import com.elanrif.springbootstarterkit.dto.auth.LoginDto;
import com.elanrif.springbootstarterkit.dto.auth.RegisterDto;
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

    @PostMapping("/login")
    public UserDto login(@Valid @RequestBody LoginDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterDto dto) {
        return authService.register(dto);
    }

    @PatchMapping("/change-password/{id}")
    public UserDto changePassword(
            @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDto dto) {
        return authService.changePassword(id, dto);
    }
}

