package com.elanrif.springbootstarterkit.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductUpdateDto(
        @Size(max = 200) String name,
        @Size(max = 200) String slug,
        @Size(max = 2000) String description,
        @Positive BigDecimal price,
        @Min(0) Integer stock,
        Boolean isActive,
        Long categoryId
) {
}

