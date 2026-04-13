package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;

public interface AuthService {

    AuthDto.Response login(AuthDto.LoginRequest request);

    AuthDto.Response register(AuthDto.RegisterRequest request);

    UserDto.Response update(AuthDto.ProfileUpdateRequest request);

    UserDto.Response resetPassword(AuthDto.ResetPasswordRequest request);

    UserDto.Response changePasswordProfile(AuthDto.ChangePasswordRequest request);
}
