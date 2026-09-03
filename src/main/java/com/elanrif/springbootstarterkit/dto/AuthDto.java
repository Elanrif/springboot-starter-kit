package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public final class AuthDto {
    private AuthDto() {}

    // === REQUESTS ===

    @Schema(name = "LoginRequest")
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    @Schema(name = "RegisterRequest")
    public record RegisterRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 255) String password,
            @URL @Size(max = 255) String avatarUrl,
            @Pattern(
                    regexp = "^(?:(?:\\+212|00212)\\s?[5-7]\\d{2}\\s?\\d{3}\\s?" +
                            "\\d{3}|0[5-7]\\d{2}\\s?\\d{3}\\s?\\d{3})$",
                    message = "must be a valid Moroccan phone number"
            )
            String phoneNumber
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
            @URL @Size(max = 255) String avatarUrl,
            @Pattern(
                    regexp = "^(?:(?:\\+212|00212)\\s?[5-7]\\d{2}\\s?\\d{3}\\s?" +
                            "\\d{3}|0[5-7]\\d{2}\\s?\\d{3}\\s?\\d{3})$",
                    message = "must be a valid Moroccan phone number"
            )
            String phoneNumber
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
            int numberOfAddresses,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
