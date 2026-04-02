package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public final class UserDto {
    private UserDto() {}

    // === REQUESTS ===

    public record CreateRequest(
            @Size(max = 100) String firstName,
            @Size(max = 100) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 255) String password,
            @Size(max = 50) String phoneNumber,
            @URL @Size(max = 255) String avatarUrl,
            Boolean isActive
    ) {}

    public record UpdateRequest(
            @Size(max = 100) String firstName,
            @Size(max = 100) String lastName,
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 8, max = 255) String password,
            @Size(max = 50) String phoneNumber,
            @URL @Size(max = 255) String avatarUrl
    ) {}

    // === RESPONSES ===

    /**
     * Summary léger pour embedded dans d'autres DTOs (ex: author d'un Post/Comment)
     */
    public record Summary(
            Long id,
            String firstName,
            String lastName,
            String avatarUrl,
            String email
    ) {}

    /**
     * Response complète pour GET /users/{id}
     */
    public record Response(
            Long id,
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String avatarUrl,
            UserRole role,
            Boolean isActive,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
