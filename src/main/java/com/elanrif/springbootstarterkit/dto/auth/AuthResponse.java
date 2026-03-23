package com.elanrif.springbootstarterkit.dto.auth;

import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") Long expiresIn,
        @JsonProperty("refresh_expires_in") Long refreshExpiresIn,
        @JsonProperty("token_type") String tokenType,
        UserDto user
) {
    public AuthResponse(String accessToken, String refreshToken, Long expiresIn, Long refreshExpiresIn, String tokenType) {
        this(accessToken, refreshToken, expiresIn, refreshExpiresIn, tokenType, null);
    }
}
