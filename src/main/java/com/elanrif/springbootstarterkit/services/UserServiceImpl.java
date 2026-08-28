package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.dto.CommonDto;
import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.AddressMapper;
import com.elanrif.springbootstarterkit.mapper.UserMapper;
import com.elanrif.springbootstarterkit.repository.AddressRepository;
import com.elanrif.springbootstarterkit.repository.UserRepository;
import com.elanrif.springbootstarterkit.specification.AddressSpecification;
import com.elanrif.springbootstarterkit.specification.UserSpecification;
import com.elanrif.springbootstarterkit.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDto.Response createUser(UserDto.CreateRequest request) {
        log.debug("Creating user with email: {}", request.email());

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);

        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserDto.Response> getUsers(
            UserDto.Filter filter,
            CommonDto.Pagination pagination
    ) {
        log.debug(
                "Fetching users - page: {}, size: {}, role: {}, status: {}",
                pagination.page(),
                pagination.size(),
                filter != null ? filter.role() : null,
                filter != null ? filter.status() : null
        );

        Page<UserDto.Response> users = userRepository
                .findAll(
                        UserSpecification.from(filter),
                        pagination.toPageable()
                )
                .map(userMapper::toResponse);

        log.debug(
                "Found {} users (total: {})",
                users.getNumberOfElements(),
                users.getTotalElements()
        );

        return PageResponse.from(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.Response getById(Long id) {
        log.debug("Fetching user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "User not found: " + id
                    );
                });

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.AddressesResponse getAddresses(
            Long userId,
            AddressDto.Filter filter,
            CommonDto.Pagination pagination
    ) {
        log.debug(
                "Fetching addresses for user {} - page: {}, size: {}, city: {}, country: {}",
                userId,
                pagination.page(),
                pagination.size(),
                filter != null ? filter.city() : null,
                filter != null ? filter.country() : null
        );

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", userId);
                    return new ResourceNotFoundException(
                            "User not found: " + userId
                    );
                });

        Page<AddressDto.Response> addresses = addressRepository
                .findAll(
                        AddressSpecification.from(userId, filter),
                        pagination.toPageable()
                )
                .map(addressMapper::toResponse);

        log.debug(
                "Found {} addresses for user {} (total: {})",
                addresses.getNumberOfElements(),
                userId,
                addresses.getTotalElements()
        );

        return userMapper.toAddressesResponse(
                user,
                PageResponse.from(addresses)
        );
    }

    @Override
    @Transactional
    public UserDto.Response updateUser(
            Long id,
            UserDto.UpdateRequest request
    ) {
        log.debug("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed - user not found with id: {}", id);
                    return new ResourceNotFoundException(
                            "User not found: " + id
                    );
                });

        userMapper.updateFromRequest(request, user);

        User updatedUser = userRepository.save(user);

        log.info("User updated successfully with id: {}", id);

        return userMapper.toResponse(updatedUser);
    }

    // Admin only
    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.debug("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("Delete failed - user not found with id: {}", id);
            throw new ResourceNotFoundException(
                    "User not found: " + id
            );
        }

        userRepository.deleteById(id);

        log.info("User deleted successfully with id: {}", id);
    }
}
