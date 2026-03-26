package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class AuthDto {
    private AuthDto() {}

    // === REQUESTS ===

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record RegisterRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 255) String password,
            @Size(max = 50) String phoneNumber,
            @Size(max = 255) String avatarUrl
    ) {}

    public record ProfileUpdateRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @Size(max = 50) String phoneNumber,
            @Size(max = 255) String avatarUrl
    ) {}

    public record ChangePasswordRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(max = 50) String oldPassword,
            @NotBlank @Size(max = 50) String newPassword
    ) {}

    public record ResetPasswordRequest(
            @NotBlank String code,
            @NotBlank String resetToken,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 255) String newPassword
    ) {}

    // === RESPONSES ===

    public record Response(
            Long id,
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String avatarUrl,
            UserRole role,
            Boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
