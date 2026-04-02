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

    /**
     * Summary léger pour embedded dans PostDto.DetailResponse
     */
    public record Summary(
            Long id,
            String content,
            UserDto.Summary author,
            LocalDateTime createdAt
    ) {}

    /**
     * Response standard pour GET /comments
     */
    public record Response(
            Long id,
            String content,
            Long postId,
            UserDto.Summary author,
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
