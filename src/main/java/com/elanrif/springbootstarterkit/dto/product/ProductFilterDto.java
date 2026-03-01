package com.elanrif.springbootstarterkit.dto.product;

public record ProductFilterDto(
        String search,
        Long categoryId,
        Boolean isActive,
        String sortBy
) {
}

