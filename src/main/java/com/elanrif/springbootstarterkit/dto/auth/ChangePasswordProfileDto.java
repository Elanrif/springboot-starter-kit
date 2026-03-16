package com.elanrif.springbootstarterkit.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordProfileDto(
                                    @NotBlank @Email @Size(max = 255) String email,
                                    @NotBlank @Size(max = 50) String oldPassword,
                                    @NotBlank @Size(max = 50) String newPassword) {
}
