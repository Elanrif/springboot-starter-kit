package com.elanrif.springbootstarterkit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

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
            @URL @Size(max = 255) String avatarUrl
    ) {}

    public record RefreshTokenRequest(
            @JsonProperty("refresh_token")
            @NotBlank String refreshToken
    ) {}

    public record ProfileUpdateRequest(
            @NotBlank @Size(max = 200) String firstName,
            @NotBlank @Size(max = 200) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @Size(max = 50) String phoneNumber,
            @URL @Size(max = 255) String avatarUrl
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

    public record TokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_in") Long expiresIn,
            @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("scope") String scope
    ) {}

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
