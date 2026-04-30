package com.elanrif.springbootstarterkit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public final class PostDto {
    private PostDto() {}

    // === REQUESTS ===

    public record CreateRequest(
            @NotBlank @Size(max = 200) String title,
            @Size(max = 200) String imageUrl,
            @Size(max = 2000) String description,
            @PositiveOrZero Long likes,
            Long authorId
    ) {}

    public record UpdateRequest(
            @Size(max = 200) String title,
            @Size(max = 200) String imageUrl,
            @Size(max = 2000) String description,
            @PositiveOrZero Long likes,
            Long authorId
    ) {}

    // === RESPONSES ===

    /**
     * Summary léger pour les listes (GET /posts)
     * Utilise UserDto.Summary pour l'auteur
     */
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
     * Response standard pour GET /posts (liste)
     */
    public record Response(
            Long id,
            String title,
            String imageUrl,
            String description,
            Long likes,
            UserDto.Summary author,
            int commentCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    /**
     * DetailResponse complète pour GET /posts/{id} (avec commentaires)
     */
    public record DetailResponse(
            Long id,
            String title,
            String imageUrl,
            String description,
            Long likes,
            UserDto.Summary author,
            List<CommentDto.Response> comments,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // === FILTER ===

    public record Filter(
            String search,
            Long authorId,
            String sort
    ) {}
}
