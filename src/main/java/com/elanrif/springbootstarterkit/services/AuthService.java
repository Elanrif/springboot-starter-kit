package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;

public interface AuthService {

    AuthDto.Response login(AuthDto.LoginRequest request);

    AuthDto.Response register(AuthDto.RegisterRequest request);

    UserDto.Response updateMyProfile(
            AuthDto.ProfileUpdateRequest request
    );

    void changeMyPassword(
            AuthDto.ChangePasswordRequest request
    );

    void resetMyPassword(
            AuthDto.ResetPasswordRequest request
    );

    void deleteMyAccount(
            AuthDto.DeleteAccountRequest request
    );
}