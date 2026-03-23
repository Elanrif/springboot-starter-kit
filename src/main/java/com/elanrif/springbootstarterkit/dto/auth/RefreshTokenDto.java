package com.elanrif.springbootstarterkit.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenDto(
        @NotBlank String refreshToken
) {
}
