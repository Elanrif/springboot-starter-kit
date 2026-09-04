package com.elanrif.springbootstarterkit.services.auth;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    UserDto.Response register(AuthDto.RegisterRequest request);
    UserDto.Response login(AuthDto.LoginRequest request,
                           HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse);
    void logout(HttpServletRequest httpRequest,
                HttpServletResponse httpResponse);
    void forgotPassword(AuthDto.ForgotPasswordRequest request);
    void resetMyPassword(AuthDto.ResetPasswordRequest request);
}