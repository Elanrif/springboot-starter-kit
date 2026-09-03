package com.elanrif.springbootstarterkit.dto;

import com.elanrif.springbootstarterkit.dto.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class CommentDto {
    private CommentDto() {}

    // === REQUESTS ===
    public interface CommentFields {
        @NotBlank(groups = OnCreate.class)
        @Pattern(groups = OnCreate.class, regexp = ".*\\S.*", message = "must not be blank")
        @Size(max = 2000) String content();
    }

    @Schema(name = "CommentCreateRequest")
    public record CreateRequest(
            String content,
            @NotNull Long postId,
            @NotNull Long authorId
    ) implements CommentFields {}

    @Schema(name = "CommentUpdateRequest")
    public record UpdateRequest(
            String content
    ) implements CommentFields {}

    // === RESPONSES ===

    @Schema(name = "CommentSummary")
    public record Summary(
            Long id,
            String content,
            UserDto.Summary author,
            LocalDateTime createdAt
    ) {}

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
            Long authorId,
            Long postId,
            String search
    ) {}
}
