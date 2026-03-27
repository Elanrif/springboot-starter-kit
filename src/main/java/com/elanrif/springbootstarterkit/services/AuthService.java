package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.AuthMapper;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
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
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenValidator resetTokenValidator;

    public AuthDto.Response login(AuthDto.LoginRequest request) {
        log.debug("Login attempt for email: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new BadRequestException("User account is inactive");
        }

        log.info("User logged in successfully: {}", request.email());
        return authMapper.toResponse(user);
    }

    @Transactional
    public AuthDto.Response register(AuthDto.RegisterRequest request) {
        log.debug("Registration attempt for email: {}", request.email());

        if (userRepository.findByEmail(request.email()).isPresent()) {
            log.warn("Registration failed - user already exists: {}", request.email());
            throw new BadRequestException("User already exists with email: " + request.email());
        }

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .avatarUrl(request.avatarUrl())
                .role(UserRole.USER)
                .isActive(true)
                .build();
        userRepository.save(user);

        log.info("User registered successfully: {}", request.email());
        return authMapper.toResponse(user);
    }

    /* --------------------------------- UserDto ----------------------------*/
    public UserDto.Response update(AuthDto.ProfileUpdateRequest request) {
        log.debug("Profile update request for email: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Profile update failed - user not found: {}", request.email());
                    return new ResourceNotFoundException("User not found for subject");
                });
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setAvatarUrl(request.avatarUrl());
        UserDto.Response response = userMapper.toResponse(userRepository.save(user));
        log.info("Profile updated successfully for user: {}", request.email());
        return response;
    }

    public UserDto.Response resetPassword(AuthDto.ResetPasswordRequest request) {
        log.debug("Password reset attempt for email: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Password reset failed - user not found: {}", request.email());
                    return new ResourceNotFoundException("User not found: " + request.email());
                });

        var tokenValid = resetTokenValidator.isValidToken(request.code(), request.resetToken());
        if (!tokenValid) {
            log.warn("Password reset failed - invalid or expired token for user: {}", request.email());
            throw new IllegalArgumentException("Token invalid or expired.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        User updatedUser = userRepository.save(user);

        log.info("Password reset successfully for user: {}", request.email());
        return userMapper.toResponse(updatedUser);
    }

    public UserDto.Response changePasswordProfile(AuthDto.ChangePasswordRequest request) {
        log.debug("Password change attempt for email: {}", request.email());
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Password change failed - user not found: {}", request.email());
                    return new ResourceNotFoundException("User not found: " + request.email());
                });

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            log.warn("Password change failed - incorrect old password for user: {}", request.email());
            throw new BadRequestException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        User updatedUser = userRepository.save(user);

        log.info("Password changed successfully for user: {}", request.email());
        return userMapper.toResponse(updatedUser);
    }
}
