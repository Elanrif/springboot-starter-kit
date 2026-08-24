package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.List;

public final class AuthDto {
    private AuthDto() {}

    // === REQUESTS ===

    @Schema(name = "UserCreateRequest")
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    @Schema(name = "UserCreateRequest")
    public record RegisterRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 255) String password,
            @Size(max = 50) String phoneNumber,
            @URL @Size(max = 255) String avatarUrl
    ) {}

    @Schema(name = "RefreshTokenRequest")
    public record RefreshTokenRequest(
            @JsonProperty("refresh_token")
            @NotBlank String refreshToken
    ) {}

    @Schema(name = "ProfileUpdateRequest")
    public record ProfileUpdateRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @Size(max = 50) String phoneNumber,
            @URL @Size(max = 255) String avatarUrl
    ) {}

    @Schema(name = "ChangePasswordRequest")
    public record ChangePasswordRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(max = 50) String currentPassword,
            @NotBlank @Size(max = 50) String newPassword
    ) {}

    @Schema(name = "ResetPasswordRequest")
    public record ResetPasswordRequest(
            @NotBlank String code,
            @NotBlank String resetToken,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 255) String newPassword
    ) {}

    @Schema(name = "DeleteAccountRequest")
    public record DeleteAccountRequest(
            @NotBlank @Email String emailInput,
            @NotBlank String messageInput
    ) {}

    // === RESPONSES ===
    @Schema(name = "UserResponse")
    public record Response(
            Long id,
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String avatarUrl,
            UserRole role,
            UserStatus status,
            List<AddressDto.Response> addresses,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
