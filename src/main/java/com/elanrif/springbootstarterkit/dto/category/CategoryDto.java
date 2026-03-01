package com.elanrif.springbootstarterkit.dto.category;

import java.time.LocalDateTime;

public record CategoryDto(
        Long id,
        String name,
        String slug,
        String description,
        String imageUrl,
        Boolean isActive,
        Integer sortOrder,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
