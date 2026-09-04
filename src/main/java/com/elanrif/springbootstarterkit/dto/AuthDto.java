package com.elanrif.springbootstarterkit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDto {
    private AuthDto() {}

    @Schema(name = "LoginRequest")
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    @Schema(name = "RegisterRequest")
    public record RegisterRequest(
            String firstName, String lastName, String email, String phoneNumber,
            @NotBlank @Size(min = 5, max = 255) String password // TODO: min = 8
    ) implements ProfileFields {}

    @Schema(name = "RefreshTokenRequest")
    public record RefreshTokenRequest(
            @JsonProperty("refresh_token") @NotBlank String refreshToken
    ) {}

    @Schema(name = "ForgotPasswordRequest")
    public record ForgotPasswordRequest(@NotBlank @Email String email) {}

    @Schema(name = "ResetPasswordRequest")
    public record ResetPasswordRequest(
            @NotBlank String code, @NotBlank String resetToken,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 5, max = 255) String newPassword // TODO: min = 8
    ) {}
}