package com.elanrif.springbootstarterkit.dto.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductCreateDto(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 200) String slug,
        @Size(max = 2000) String description,
        @NotNull @Positive BigDecimal price,
        @NotNull @Min(0) Integer stock,
        @NotNull Boolean isActive,
        Long categoryId
) {
}
