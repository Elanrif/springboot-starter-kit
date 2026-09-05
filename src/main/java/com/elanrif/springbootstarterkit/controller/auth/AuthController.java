package com.elanrif.springbootstarterkit.controller.auth;

import com.elanrif.springbootstarterkit.dto.auth.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.services.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and registration")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Create a new account",
            description = "Registers a new user with the USER role and INACTIVE status."
    )
    public ResponseEntity<UserDto.Response> register(
            @Valid @RequestBody AuthDto.RegisterRequest request
    ) {
        UserDto.Response response = authService.register(request);
        log.info("POST /api/v1/auth/register - Account created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Log in a user",
            description = "Authenticates a user with email and password and starts a session " +
                    "(session cookie returned in the response)."
    )
    public ResponseEntity<UserDto.Response> login(
            @Valid @RequestBody AuthDto.LoginRequest request,
            // ⚠️ gives access to the incoming request — needed to create/find the HTTP session
            HttpServletRequest httpRequest,
            // ⚠️ gives access to the outgoing response — needed to write the Set-Cookie header
            HttpServletResponse httpResponse
    ) {
        UserDto.Response response = authService.login(request, httpRequest, httpResponse);
        log.info("POST /api/v1/auth/login - Login successful for: {}", request.email());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Log out the current user",
            description = "Invalidates the current session and clears the session cookie."
    )
    public ResponseEntity<Void> logout(
            // ⚠️ gives access to the incoming request — needed to invalidate the HTTP session
            HttpServletRequest httpRequest,
            // ⚠️ gives access to the outgoing response — needed to clear the Set-Cookie header
            HttpServletResponse httpResponse
    ) {
        authService.logout(httpRequest, httpResponse);
        log.info("POST /api/v1/auth/logout - Logout requested");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Send a password reset link/token by email",
            description = "Sends a reset token to the given email if an account exists for it. " +
                    "Does not confirm whether the email is registered, for security reasons."
    )
    public ResponseEntity<Void> forgotPassword(
            // ⚠️ gives access to the incoming request — needed to process the forgot password request
            @Valid @RequestBody AuthDto.ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        log.info("POST /api/v1/auth/forgot-password - Reset requested for: {}", request.email());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password using the token received by email",
            description = "Sets a new password after validating the code/token sent by email."
    )
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody AuthDto.ResetPasswordRequest request
    ) {
        authService.resetMyPassword(request);
        log.info("POST /api/v1/auth/reset-password - Password reset for: {}", request.email());
        return ResponseEntity.noContent().build();
    }
}