package com.elanrif.springbootstarterkit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public final class PostDto {

    private PostDto() {}

    // =========================================================
    // REQUESTS
    // =========================================================
    // @Notblank means the value must not be blank
    // ❌ exp --> [ null, "", "   " ], are all considered blank
    // @Pattern means the value must match the given regular expression
    // =========================================================

    public interface PostFields {
        @Size(max = 200) String title();
        @Size(max = 2000) String description();
        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        @URL @Size(max = 200) String imageUrl();
    }

    @Schema(name = "PostCreateRequest")
    public record CreateRequest(
            @NotBlank String title,
            @NotBlank String description,
            String imageUrl,
            @NotNull Long authorId
    ) implements PostFields {}

    @Schema(name = "PostUpdateRequest")
    public record UpdateRequest(
            @Pattern(regexp = ".*\\S.*", message = "must not be blank")
            String title,
            @Pattern(regexp = ".*\\S.*", message = "must not be blank")
            String description,
            String imageUrl
    ) implements PostFields {}

    // =========================================================
    // RESPONSES
    // =========================================================

    @Schema(name = "PostSummary")
    public record Summary(

            Long id,
            String title,
            String imageUrl,
            Long likes,
            UserDto.Summary author,
            int numberOfComments,
            LocalDateTime createdAt

    ) {}

    @Schema(name = "PostResponse")
    public record Response(

            Long id,
            String title,
            String imageUrl,
            String description,
            Long likes,
            UserDto.Summary author,
            int numberOfComments,
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
