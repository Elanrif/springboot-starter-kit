package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.auth.*;
import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
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
    private final PasswordEncoder passwordEncoder;
    private final ResetTokenValidator resetTokenValidator;
    private final KeycloakService keycloakService;

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
