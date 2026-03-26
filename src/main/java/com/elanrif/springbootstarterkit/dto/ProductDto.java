package com.elanrif.springbootstarterkit.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class ProductDto {
    private ProductDto() {}

    // === REQUESTS ===

    public record CreateRequest(
            @NotBlank @Size(max = 200) String name,
            @NotBlank @Size(max = 200) String slug,
            @Size(max = 2000) String description,
            @NotNull @Positive BigDecimal price,
            @NotNull @Min(0) Integer stock,
            @NotNull Boolean isActive,
            Long categoryId
    ) {}

    public record UpdateRequest(
            @Size(max = 200) String name,
            @Size(max = 200) String slug,
            @Size(max = 2000) String description,
            @Positive BigDecimal price,
            @Min(0) Integer stock,
            Boolean isActive,
            Long categoryId
    ) {}

    // === RESPONSES ===

    public record Response(
            Long id,
            String name,
            String slug,
            String description,
            BigDecimal price,
            Integer stock,
            Boolean isActive,
            CategoryDto.Response category,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // === FILTER ===

    public record Filter(
            String search,
            Long categoryId,
            Boolean isActive,
            String sortBy
    ) {}
}
