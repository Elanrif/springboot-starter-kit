package com.elanrif.springbootstarterkit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class CommentDto {
    private CommentDto() {}

    // === REQUESTS ===

    @Schema(name = "CommentCreateRequest")
    public record CreateRequest(
            @NotBlank @Size(max = 2000) String content,
            @NotNull Long postId,
            @NotNull Long authorId
    ) {}

    @Schema(name = "CommentUpdateRequest")
    public record UpdateRequest(
            @Size(max = 2000) String content
    ) {}

    // === RESPONSES ===

    /**
     * Summary léger pour embedded dans PostDto.DetailResponse
     */
    @Schema(name = "CommentSummary")
    public record Summary(
            Long id,
            String content,
            UserDto.Summary author,
            LocalDateTime createdAt
    ) {}

    /**
     * Response standard pour GET /comments
     */
    @Schema(name = "CommentResponse")
    public record Response(
            Long id,
            String content,
            Long postId,
            UserDto.Summary author,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // === FILTER ===

    @Schema(name = "CommentFilter")
    public record Filter(
            String search,
            Long postId,
            Long authorId,
            String sort
    ) {}
}