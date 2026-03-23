package com.elanrif.springbootstarterkit.dto.auth;

import com.elanrif.springbootstarterkit.dto.user.UserDto;

public record KeycloakAuthResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        Long refreshExpiresIn,
        String tokenType,
        String scope,
        UserDto user
) {
    public static KeycloakAuthResponse from(KeycloakTokenResponse token, UserDto user) {
        return new KeycloakAuthResponse(
                token.accessToken(),
                token.refreshToken(),
                token.expiresIn(),
                token.refreshExpiresIn(),
                token.tokenType(),
                token.scope(),
                user
        );
    }
}
