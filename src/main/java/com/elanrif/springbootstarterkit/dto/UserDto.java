package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.dto.validation.OnCreate;
import com.elanrif.springbootstarterkit.dto.validation.OnUpdate;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public final class UserDto {
    private UserDto() {}

    // =========================================================
    // REQUESTS
    // =========================================================
    // @Notblank means the value must not be blank
    // ❌ exp --> [ null, "", "   " ], are all considered blank
    // @Pattern means the value must match the given regular expression
    // =========================================================

    @Schema(name = "UserRequest")
    public record Request(

            @NotBlank(groups = OnCreate.class)
            @Pattern(groups = OnUpdate.class, regexp = ".*\\S.*", message = "must not be blank")
            @Size(max = 100)
            String firstName,

            @NotBlank(groups = OnCreate.class)
            @Pattern(groups = OnUpdate.class, regexp = ".*\\S.*", message = "must not be blank")
            @Size(max = 100)
            String lastName,

            @NotBlank(groups = OnCreate.class)
            @Pattern(groups = OnUpdate.class, regexp = ".*\\S.*", message = "must not be blank")
            @Email
            @Size(max = 255)
            String email,

            @NotBlank(groups = OnCreate.class)
            @Pattern(groups = OnUpdate.class, regexp = ".*\\S.*", message = "must not be blank")
            @Size(min = 5, max = 255) // TODO: min = 8
            String password,

            @URL
            @Pattern(groups = OnUpdate.class, regexp = ".*\\S.*", message = "must not be blank")
            @Size(max = 255)
            String avatarUrl,

            @Size(max = 50)
            @Pattern(
                    regexp = "^(?:(?:\\+212|00212)\\s?[5-7]\\d{2}\\s?\\d{3}\\s?" +
                            "\\d{3}|0[5-7]\\d{2}\\s?\\d{3}\\s?\\d{3})$",
                    message = "must be a valid Moroccan phone number"
            )
            String phoneNumber,

            // ❌ Pattern can be used to validate the format of a string.
            UserRole role,
            UserStatus status
    ) {}


    // === RESPONSES ===

    @Schema(name = "UserSummary")
    public record Summary(
            Long id,
            String firstName,
            String lastName,
            String avatarUrl,
            String email
    ) {}

    @Schema(name = "UserResponse")
    public record Response(
            Long id,
            String email,
            String firstName,
            String lastName,
            String phoneNumber,
            String avatarUrl,
            UserRole role,
            UserStatus status,
            int numberOfAddresses,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // === FILTERS ===

    public enum Ghosts {
        EXCLUDE, // comportement par défaut : cache les comptes supprimés
        ONLY,    // uniquement les comptes supprimés
        INCLUDE  // actifs + supprimés, pas de filtre
    }

    @Schema(name = "UserFilter")
    public record Filter(
            UserRole role,
            UserStatus status,
            Ghosts ghosts
    ) {}
}
