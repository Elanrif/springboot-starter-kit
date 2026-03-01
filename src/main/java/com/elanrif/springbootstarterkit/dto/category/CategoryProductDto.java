package com.elanrif.springbootstarterkit.dto.category;

import com.elanrif.springbootstarterkit.dto.product.ProductDto;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryProductDto(
        Long id,
        String name,
        String slug,
        String description,
        List<ProductDto> products,
        String imageUrl,
        Boolean isActive,
        Integer sortOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
