package com.elanrif.springbootstarterkit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public final class CurrentUserDto {
    private CurrentUserDto() {}

    @Schema(name = "UpdateProfileRequest")
    public record UpdateProfileRequest(
            String firstName, String lastName, String email, String phoneNumber,
            @URL @Size(max = 255) String avatarUrl
    ) implements ProfileFields {}

    @Schema(name = "ChangePasswordRequest")
    public record ChangePasswordRequest(
            @NotBlank @Size(max = 50) String currentPassword,
            @NotBlank @Size(min = 5, max = 255) String newPassword // TODO: min = 8
    ) {}

    @Schema(name = "DeleteAccountRequest")
    public record DeleteAccountRequest(@NotBlank String message) {}
}