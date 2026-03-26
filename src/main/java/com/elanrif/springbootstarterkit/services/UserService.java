package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto.Response createUser(UserDto.CreateRequest request) {
        log.debug("Creating user with email: {}", request.email());
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    public UserDto.Response update(Long id, UserDto.UpdateRequest request) {
        log.debug("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed - user not found with id: {}", id);
                    return new ResourceNotFoundException("User not found for subject");
                });
        userMapper.updateFromRequest(request, user);
        UserDto.Response response = userMapper.toResponse(userRepository.save(user));
        log.info("User updated successfully with id: {}", id);
        return response;
    }

    public List<UserDto.Response> getAll() {
        log.debug("Fetching all users");
        List<UserDto.Response> users = userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
        log.debug("Found {} users", users.size());
        return users;
    }

    public UserDto.Response getById(Long id) {
        log.debug("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found: " + id);
                });
        return userMapper.toResponse(user);
    }

    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Delete failed - user not found with id: {}", id);
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    public List<UserDto.Response> searchUsers(String email, String firstName, String lastName, Boolean isActive) {
        log.debug("Searching users with filters - email: {}, firstName: {}, lastName: {}, isActive: {}",
                email, firstName, lastName, isActive);
        String emailParam     = email     != null ? "%" + email.toLowerCase()     + "%" : null;
        String firstNameParam = firstName != null ? "%" + firstName.toLowerCase() + "%" : null;
        String lastNameParam  = lastName  != null ? "%" + lastName.toLowerCase()  + "%" : null;

        List<UserDto.Response> users = userRepository.searchUsers(emailParam, firstNameParam, lastNameParam, isActive)
                .stream()
                .map(userMapper::toResponse)
                .toList();
        log.debug("Search returned {} users", users.size());
        return users;
    }
}
