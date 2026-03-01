package com.elanrif.springbootstarterkit.dto.category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryCreateDto(
        @NotBlank @Size(max = 200) String name,
        @NotBlank @Size(max = 200) String slug,
        @Size(max = 2000) String description,
        @Size(max = 255) String imageUrl,
        @NotNull Boolean isActive,
        @Min(0) Integer sortOrder
) {
}

