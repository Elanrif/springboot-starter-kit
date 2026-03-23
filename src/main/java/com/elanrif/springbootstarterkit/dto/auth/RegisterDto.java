package com.elanrif.springbootstarterkit.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterDto(
        @NotBlank @Size(max = 200) String firstName,
        @NotBlank @Size(max = 200) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        @Size(max = 50) String phoneNumber,
        @Size(max = 255) String avatarUrl
) {
}

