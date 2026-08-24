package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public final class AuthDto {
    private AuthDto() {}

    // === REQUESTS ===

    @Schema(name = "AuthLoginRequest")
    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    @Schema(name = "AuthRegisterRequest")
    public record RegisterRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 255) String password,
            @Size(max = 50) String phoneNumber,
            @URL @Size(max = 255) String avatarUrl
    ) {}

    @Schema(name = "AuthRefreshTokenRequest")
    public record RefreshTokenRequest(
            @JsonProperty("refresh_token")
            @NotBlank String refreshToken
    ) {}

    @Schema(name = "AuthProfileUpdateRequest")
    public record ProfileUpdateRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @Size(max = 50) String phoneNumber,
            @URL @Size(max = 255) String avatarUrl
    ) {}

    @Schema(name = "AuthChangePasswordRequest")
    public record ChangePasswordRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(max = 50) String oldPassword,
            @NotBlank @Size(max = 50) String newPassword
    ) {}

    @Schema(name = "AuthResetPasswordRequest")
    public record ResetPasswordRequest(
            @NotBlank String code,
            @NotBlank String resetToken,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 255) String newPassword
    ) {}

    // === RESPONSES ===

    @Schema(name = "AuthTokenResponse")
    public record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_in") Long expiresIn,
            @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("scope") String scope
    ) {}

    @Schema(name = "AuthLoginResponse")
    public record Response(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_in") Long expiresIn,
            @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
            @JsonProperty("token_type") String tokenType,
            String scope,
            UserDto.Response user
    ) {
        public static Response from(TokenResponse tokenResponse, UserDto.Response user) {
            return new Response(
                    tokenResponse.accessToken(),
                    tokenResponse.refreshToken(),
                    tokenResponse.expiresIn(),
                    tokenResponse.refreshExpiresIn(),
                    tokenResponse.tokenType(),
                    tokenResponse.scope(),
                    user
            );
        }
    }
}
