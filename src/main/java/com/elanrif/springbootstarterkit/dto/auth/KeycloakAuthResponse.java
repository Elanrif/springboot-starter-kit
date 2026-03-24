package com.elanrif.springbootstarterkit.dto.auth;

import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KeycloakAuthResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
        @JsonProperty("token_type") String tokenType,
        String scope,
        UserDto user
) {
    public static KeycloakAuthResponse from(KeycloakTokenResponse tokenResponse, UserDto user) {
        return new KeycloakAuthResponse(
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
