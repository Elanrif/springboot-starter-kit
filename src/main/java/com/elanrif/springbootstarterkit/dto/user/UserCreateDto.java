package com.elanrif.springbootstarterkit.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateDto(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 255) String password,
        @Size(max = 255) String avatarUrl,
        Boolean isActive
) {
}
