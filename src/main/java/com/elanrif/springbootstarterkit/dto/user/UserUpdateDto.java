package com.elanrif.springbootstarterkit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record UserUpdateDto(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        @Size(max = 50) String phoneNumber,
        @Size(max = 255) String avatarUrl
) {
}

