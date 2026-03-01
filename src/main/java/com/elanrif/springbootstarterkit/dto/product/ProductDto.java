package com.elanrif.springbootstarterkit.dto.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDto(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        Integer stock,
        Boolean isActive,
        Long categoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

