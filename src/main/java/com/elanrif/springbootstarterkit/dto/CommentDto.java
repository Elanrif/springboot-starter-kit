package com.elanrif.springbootstarterkit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class CommentDto {
    private CommentDto() {}

    // === REQUESTS ===

    public record CreateRequest(
            @NotBlank @Size(max = 2000) String content,
            @NotNull Long postId,
            @NotNull Long authorId
    ) {}

    public record UpdateRequest(
            @Size(max = 2000) String content
    ) {}

    // === RESPONSES ===

    public record Response(
            Long id,
            String content,
            Long postId,
            UserDto.Response author,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // === FILTER ===

    public record Filter(
            String search,
            Long postId,
            Long authorId,
            String sortBy
    ) {}
}
