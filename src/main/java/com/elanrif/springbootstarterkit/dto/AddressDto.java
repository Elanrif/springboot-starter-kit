package com.elanrif.springbootstarterkit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressDto {

    @Schema(name = "AddressCreateRequest")
    public record CreateRequest(
            @NotBlank @Size(max = 255) String street,
            @NotBlank @Size(max = 20) String postalCode,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 100) String country,
            Boolean defaultAddress
    ) {}

    @Schema(name = "AddressUpdateRequest")
    public record UpdateRequest(
            @NotBlank @Size(max = 255) String street,
            @NotBlank @Size(max = 20) String postalCode,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Size(max = 100) String country,
            Boolean defaultAddress
    ) {}

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
}