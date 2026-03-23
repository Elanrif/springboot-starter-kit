package com.elanrif.springbootstarterkit.dto.auth;

import com.elanrif.springbootstarterkit.dto.user.UserDto;

public record KeycloakAuthResponse(
        TokenInfo token,
        UserDto user
) {
    public record TokenInfo(
            String accessToken,
            String refreshToken,
            Long expiresIn,
            Long refreshExpiresIn,
            String tokenType,
            String scope
    ) {}

    public static KeycloakAuthResponse from(KeycloakTokenResponse tokenResponse, UserDto user) {
        TokenInfo token = new TokenInfo(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                tokenResponse.expiresIn(),
                tokenResponse.refreshExpiresIn(),
                tokenResponse.tokenType(),
                tokenResponse.scope()
        );
        return new KeycloakAuthResponse(token, user);
    }
}
