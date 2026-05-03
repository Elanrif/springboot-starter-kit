package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.elanrif.springbootstarterkit.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto.Response createUser(UserDto.CreateRequest request) {
        log.debug("Creating user with email: {}", request.email());
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return userMapper.toResponse(savedUser);
    }

    @Override
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

    @Override
    public PageResponse<UserDto.Response> getAll(int page, int size, String sort) {
        log.debug("Fetching all users - page: {}, size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size, toSort(sort));

        Page<UserDto.Response> result = userRepository.findAll(pageable)
                .map(userMapper::toResponse);

        log.debug("Found {} users (total: {})", result.getNumberOfElements(), result.getTotalElements());
        return PageResponse.from(result);
    }

    @Override
    public UserDto.Response getById(Long id) {
        log.debug("Fetching user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found: " + id);
                });
        return userMapper.toResponse(user);
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("Delete failed - user not found with id: {}", id);
            throw new ResourceNotFoundException("User not found: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    @Override
    public PageResponse<UserDto.Response> searchUsers(String email, String firstName, String lastName, Boolean isActive, int page, int size, String sort) {
        log.debug("Searching users with filters - email: {}, firstName: {}, lastName: {}, isActive: {}, page: {}, size: {}",
                email, firstName, lastName, isActive, page, size);
        String emailParam     = email     != null ? "%" + email.toLowerCase()     + "%" : null;
        String firstNameParam = firstName != null ? "%" + firstName.toLowerCase() + "%" : null;
        String lastNameParam  = lastName  != null ? "%" + lastName.toLowerCase()  + "%" : null;

        PageRequest pageRequest = PageRequest.of(page, size, toSort(sort));
        Page<UserDto.Response> result = userRepository.searchUsers(emailParam, firstNameParam, lastNameParam, isActive, pageRequest)
                .map(userMapper::toResponse);
        log.debug("Search returned {} users (total: {})", result.getNumberOfElements(), result.getTotalElements());
        return PageResponse.from(result);
    }

    private Sort toSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by("id").ascending(); // Tri par défaut
        }

        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, field);
    }
}
