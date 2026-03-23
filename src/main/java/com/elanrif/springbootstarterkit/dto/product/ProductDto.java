package com.elanrif.springbootstarterkit.dto.product;


import com.elanrif.springbootstarterkit.dto.category.CategoryDto;

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
        CategoryDto category,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}

