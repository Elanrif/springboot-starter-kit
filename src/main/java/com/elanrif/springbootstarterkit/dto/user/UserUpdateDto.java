package com.elanrif.springbootstarterkit.dto.user;

import jakarta.validation.constraints.Size;

public record UserUpdateDto(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        @Size(max = 50) String phoneNumber,
        @Size(max = 255) String avatarUrl
) {
}

