package com.elanrif.springbootstarterkit.dto.auth;

import com.elanrif.springbootstarterkit.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;

public record LoginResponseDto(
        @NotBlank String token,
        @NotBlank String refreshToken,
        UserDto user
) {
}

