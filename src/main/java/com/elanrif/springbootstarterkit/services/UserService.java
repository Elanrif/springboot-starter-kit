package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDto.Response createUser(UserDto.CreateRequest request) {
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserDto.Response update(Long id, UserDto.UpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for subject"));
        userMapper.updateFromRequest(request, user);
        return userMapper.toResponse(userRepository.save(user));
    }

    public List<UserDto.Response> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserDto.Response getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return userMapper.toResponse(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    public List<UserDto.Response> searchUsers(String email, String firstName, String lastName, Boolean isActive) {
        String emailParam     = email     != null ? "%" + email.toLowerCase()     + "%" : null;
        String firstNameParam = firstName != null ? "%" + firstName.toLowerCase() + "%" : null;
        String lastNameParam  = lastName  != null ? "%" + lastName.toLowerCase()  + "%" : null;

        return userRepository.searchUsers(emailParam, firstNameParam, lastNameParam, isActive)
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }
}
