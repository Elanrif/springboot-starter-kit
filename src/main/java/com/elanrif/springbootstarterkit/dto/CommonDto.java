package com.elanrif.springbootstarterkit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class CommonDto {

    private CommonDto() {}

    @Schema(name = "Pagination")
    public record Pagination(
            Integer page,
            Integer size,
            String sort
    ) {

        public Pagination {
            page = page == null || page < 1 ? 1 : page;
            size = size == null || size < 1 ? 5 : size;
            sort = sort == null || sort.isBlank()
                    ? "createdAt,desc"
                    : sort;
        }

        public Pageable toPageable() {
            return PageRequest.of(
                    page - 1,
                    size,
                    toSort()
            );
        }

        private Sort toSort() {
            String[] parts = sort.split(",");

            String property = parts[0];

            Sort.Direction direction = parts.length > 1
                    ? Sort.Direction.fromString(parts[1])
                    : Sort.Direction.ASC;

            return Sort.by(direction, property);
        }
    }
}