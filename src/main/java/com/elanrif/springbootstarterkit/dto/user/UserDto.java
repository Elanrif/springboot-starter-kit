package com.elanrif.springbootstarterkit.dto.user;

import com.elanrif.springbootstarterkit.entity.UserRole;

public record UserDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        String password,
        String avatarUrl,
        UserRole role,
        Boolean isActive
) {
}

