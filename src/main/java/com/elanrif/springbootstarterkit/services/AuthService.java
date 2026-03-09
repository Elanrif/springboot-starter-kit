package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.auth.ChangePasswordDto;
import com.elanrif.springbootstarterkit.dto.auth.LoginDto;
import com.elanrif.springbootstarterkit.dto.auth.RegisterDto;
import com.elanrif.springbootstarterkit.dto.user.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.exception.BadRequestException;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto login(LoginDto dto) {
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + dto.email()));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new BadRequestException("User account is inactive");
        }

        /**
         *  Génération des tokens (implémentation simple)
         *  String token = generateToken(user);
         *  String refreshToken = generateRefreshToken(user);
         */

        return userMapper.toDto(user);
    }

    public UserDto register(@Valid RegisterDto dto) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("Email already registered: " + dto.email());
        }

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
        return userMapper.toDto(savedUser);
    }

    public UserDto changePassword(Long userId, ChangePasswordDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (!passwordEncoder.matches(dto.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid current password");
        }

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    private String generateToken(User user) {
        // Implémentation simple - À remplacer par JWT ou autre mécanisme
        return "token_" + user.getId() + "_" + UUID.randomUUID();
    }

    private String generateRefreshToken(User user) {
        // Implémentation simple - À remplacer par JWT ou autre mécanisme
        return "refresh_" + user.getId() + "_" + UUID.randomUUID();
    }
}

