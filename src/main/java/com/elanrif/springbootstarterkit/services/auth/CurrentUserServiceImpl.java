package com.elanrif.springbootstarterkit.services.auth;

import com.elanrif.springbootstarterkit.config.SecurityUtils;
import com.elanrif.springbootstarterkit.dto.CurrentUserDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private static final String MESSAGE_DELETE_ACCOUNT = "I WANT TO DELETE MY ACCOUNT";

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    private User getCurrentUser() {
        Long currentUserId = securityUtils.getCurrentUserId();
        return userRepository.findById(currentUserId)
                .orElseThrow(() -> {
                    log.warn("Current user not found with id: {}", currentUserId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });
    }

    @Override
    @Transactional
    public UserDto.Response updateMyProfile(CurrentUserDto.UpdateProfileRequest request) {
        User user = getCurrentUser();
        log.debug("Updating profile for user id: {}", user.getId());

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());
        user.setAvatarUrl(request.avatarUrl());

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user id: {}", updatedUser.getId());

        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public void changeMyPassword(CurrentUserDto.ChangePasswordRequest request) {
        User user = getCurrentUser();
        log.debug("Password change attempt for user id: {}", user.getId());

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            log.warn("Password change failed - incorrect current password for user id: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user id: {}", user.getId());
    }

    @Override
    @Transactional
    public void deleteMyAccount(CurrentUserDto.DeleteAccountRequest request) {
        User user = getCurrentUser();
        log.debug("Account deletion attempt for user id: {}", user.getId());

        if (request.message() == null || !request.message().trim().equalsIgnoreCase(MESSAGE_DELETE_ACCOUNT)) {
            log.warn("Account deletion failed - incorrect confirmation message for user id: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You must type: " + MESSAGE_DELETE_ACCOUNT);
        }

        userRepository.delete(user);
        log.info("Account deleted successfully for user id: {}", user.getId());
    }
}