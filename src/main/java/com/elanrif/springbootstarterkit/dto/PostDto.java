package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.util.PageResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public final class PostDto {

    private PostDto() {}

    // =========================================================
    // REQUESTS
    // =========================================================

    @Schema(name = "PostCreateRequest")
    public record CreateRequest(
            @NotBlank
            @Size(max = 200)
            String title,
            @Size(max = 200)
            String imageUrl,
            @Size(max = 2000)
            String description,
            @PositiveOrZero
            Long likes,
            @NotNull
            Long authorId

    ) {}

    @Schema(name = "PostUpdateRequest")
    public record UpdateRequest(
            @Size(max = 200)
            String title,
            @Size(max = 200)
            String imageUrl,
            @Size(max = 2000)
            String description,
            @PositiveOrZero
            Long likes,
            @NotNull
            Long authorId

    ) {}

    // =========================================================
    // RESPONSES
    // =========================================================

    /**
     * Lightweight representation of a post.
     * Used when only a summary is required.
     */
    @Schema(name = "PostSummary")
    public record Summary(
            Long id,
            String title,
            String imageUrl,
            Long likes,
            UserDto.Summary author,
            int commentCount,
            LocalDateTime createdAt

    ) {}

    /**
     * Standard post response.
     * Used for GET /posts.
     */
    @Schema(name = "PostResponse")
    public record Response(

            Long id,
            String title,
            String imageUrl,
            String description,
            Long likes,
            UserDto.Summary author,
            int commentSize,
            LocalDateTime createdAt,
            LocalDateTime updatedAt

    ) {}

    @Schema(name = "PostCommentsResponse")
    public record CommentsResponse(
            Long id,
            String title,
            String imageUrl,
            String description,
            Long likes,
            UserDto.Summary author,
            PageResponse<CommentDto.Response> comments,
            LocalDateTime createdAt,
            LocalDateTime updatedAt

    ) {}

    // =========================================================
    // FILTER
    // =========================================================

    @Schema(name = "PostFilter")
    public record Filter(
            String search,
            Long authorId
    ) {}
}