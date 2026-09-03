package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.dto.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AddressDto {

    public interface AddressFields {
        @Size(max = 255) String street();
        @Size(max = 20) String postalCode();
        @Size(max = 100) String city();
        @Size(max = 100) String country();
        Boolean defaultAddress();
    }

    @Schema(name = "AddressCreateRequest")
    public record CreateRequest(
            @NotBlank String street,
            @NotBlank String postalCode,
            @NotBlank String city,
            @NotBlank String country,
            @NotBlank Boolean defaultAddress,
            @NotNull Long userId
    ) implements AddressFields {}

    @Schema(name = "AddressUpdateRequest")
    public record UpdateRequest(
            @Pattern(regexp = ".*\\S.*", message = "must not be blank")
            String street,
            @Pattern(regexp = ".*\\S.*", message = "must not be blank")
            String postalCode,
            @Pattern(regexp = ".*\\S.*", message = "must not be blank")
            String city,
            @Pattern(regexp = ".*\\S.*", message = "must not be blank")
            String country,
            @Pattern(regexp = ".*\\S.*", message = "must not be blank")
            Boolean defaultAddress
    ) implements AddressFields {}

    @Schema(name = "AddressResponse")
    public record Response(
            Long id,
            String street,
            String postalCode,
            String city,
            String country,
            Boolean defaultAddress,
            Long userId
    ) {}

    // =========================================================
    // FILTER
    // =========================================================

    @Schema(name = "AddressFilter")
    public record Filter(
            Long userId,
            String city,
            String country,
            Boolean isDefault
    ) {}
}
