package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.auth.*;
import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenValidator resetTokenValidator;
    private final KeycloakService keycloakService;

    /**
     * Login user via Keycloak ROPC and return tokens with user info
     */
    public AuthResponse login(LoginDto dto) {
        // First verify user exists in local DB
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.email()));

        if (!user.getIsActive()) {
            throw new BadRequestException("User account is inactive");
        }

        // Authenticate via Keycloak ROPC
        KeycloakAuthResponse authResponse = keycloakService.login(dto.email(), dto.password());

        return new AuthResponse(
                authResponse.token().accessToken(),
                authResponse.token().refreshToken(),
                authResponse.token().expiresIn(),
                authResponse.token().refreshExpiresIn(),
                authResponse.token().tokenType(),
                userMapper.toDto(user)
        );
    }

    /**
     * Register user in local DB first, then in Keycloak
     */
    @Transactional
    public AuthResponse register(@Valid RegisterDto dto) {
        // Check if user already exists in local DB
        if (userRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("Email already registered: " + dto.email());
        }

        // Save user to local DB first
        User user = User.builder()
                .email(dto.email())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .phoneNumber(dto.phoneNumber())
                .password(passwordEncoder.encode(dto.password()))
                .role(UserRole.USER)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User saved to local DB: {}", savedUser.getEmail());

        // Create user in Keycloak and auto-login
        try {
            KeycloakAuthResponse authResponse = keycloakService.createUser(dto);
            log.info("User created in Keycloak: {}", dto.email());

            return new AuthResponse(
                    authResponse.token().accessToken(),
                    authResponse.token().refreshToken(),
                    authResponse.token().expiresIn(),
                    authResponse.token().refreshExpiresIn(),
                    authResponse.token().tokenType(),
                    userMapper.toDto(savedUser)
            );
        } catch (Exception e) {
            log.error("Failed to create user in Keycloak, rolling back local DB: {}", e.getMessage());
            throw new BadRequestException("Failed to complete registration: " + e.getMessage());
        }
    }

    /**
     * Refresh access token
     */
    public AuthResponse refreshToken(String refreshToken) {
        KeycloakTokenResponse tokenResponse = keycloakService.refreshToken(refreshToken);

        return new AuthResponse(
                tokenResponse.accessToken(),
                tokenResponse.refreshToken(),
                tokenResponse.expiresIn(),
                tokenResponse.refreshExpiresIn(),
                tokenResponse.tokenType()
        );
    }

    /**
     * Logout user (invalidate refresh token in Keycloak)
     */
    public void logout(String refreshToken) {
        keycloakService.logout(refreshToken);
    }

    public UserDto update(ProfileDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for subject"));
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setEmail(dto.email());
        user.setPhoneNumber(dto.phoneNumber());
        return userMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto resetPassword(ResetPasswordDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.email()));

        var tokenValid = resetTokenValidator.isValidToken(dto.code(), dto.resetToken());
        if (!tokenValid) {
            throw new IllegalArgumentException("Token invalid or expired.");
        }

        // Update password in local DB
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        User updatedUser = userRepository.save(user);

        // Update password in Keycloak
        try {
            keycloakService.updateUserPassword(dto.email(), dto.newPassword());
        } catch (Exception e) {
            log.error("Failed to update password in Keycloak: {}", e.getMessage());
        }

        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public UserDto changePasswordProfile(ChangePasswordProfileDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.email()));

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        // Update password in local DB
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        User updatedUser = userRepository.save(user);

        // Update password in Keycloak
        try {
            keycloakService.updateUserPassword(dto.email(), dto.newPassword());
        } catch (Exception e) {
            log.error("Failed to update password in Keycloak: {}", e.getMessage());
        }

        return userMapper.toDto(updatedUser);
    }
}
