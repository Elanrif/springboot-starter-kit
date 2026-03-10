package com.elanrif.springbootstarterkit.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordDto(
        @NotBlank String code,
        @NotBlank String resetToken,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 255) String newPassword
) {
}
