package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AuthDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
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

    public UserDto.Response update(AuthDto.ProfileUpdateRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found for subject"));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserDto.Response resetPassword(AuthDto.ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.email()));

        var tokenValid = resetTokenValidator.isValidToken(dto.code(), dto.resetToken());
        if (!tokenValid) {
            throw new IllegalArgumentException("Token invalid or expired.");
        }

        // Update password in local DB
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        User updatedUser = userRepository.save(user);

        // Update password in Keycloak
        try {
            keycloakService.updateUserPassword(request.email(), request.newPassword());
        } catch (Exception e) {
            log.error("Failed to update password in Keycloak: {}", e.getMessage());
        }

        return userMapper.toResponse(updatedUser);
    }

    @Transactional
    public UserDto.Response changePasswordProfile(AuthDto.ChangePasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.email()));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        // Update password in local DB
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        User updatedUser = userRepository.save(user);

        // Update password in Keycloak
        try {
            keycloakService.updateUserPassword(request.email(), request.newPassword());
        } catch (Exception e) {
            log.error("Failed to update password in Keycloak: {}", e.getMessage());
        }

        return userMapper.toResponse(updatedUser);
    }
}
