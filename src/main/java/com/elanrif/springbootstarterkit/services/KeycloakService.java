package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AuthDto;

public interface KeycloakService {

    AuthDto.Response login(String username, String password);

    AuthDto.TokenResponse refreshToken(String refreshToken);

    AuthDto.Response createUser(AuthDto.RegisterRequest request);

    void updateUserPassword(String email, String newPassword);

    void logout(String refreshToken);
}
