package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.validation.OnCreate;
import com.elanrif.springbootstarterkit.dto.validation.OnUpdate;
import com.elanrif.springbootstarterkit.services.CommentService;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(
        name = "Comments",
        description = "Manage comments attached to posts"
)
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    @Operation(
            summary = "List comments",
            description = "Returns a paginated list of comments, optionally filtered by search text or author."
    )
    public ResponseEntity<PageResponse<CommentDto.Response>> list(
            @ModelAttribute CommentDto.Filter filter,
            @ModelAttribute PaginationDto.Pagination pagination
    ) {
        log.debug("GET /api/v1/comments - page: {}, size: {}", pagination.page(), pagination.size());

        PageResponse<CommentDto.Response> response =
                commentService.getComments(filter, pagination);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a comment by id",
            description = "Returns a single comment. Returns 404 if it does not exist."
    )
    public ResponseEntity<CommentDto.Response> getById(@PathVariable Long id) {
        log.debug("GET /api/v1/comments/{}", id);
        CommentDto.Response response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a comment",
            description = "Creates a new comment on a post, on behalf of the given author."
    )
    public ResponseEntity<CommentDto.Response> create(
            @Validated(OnCreate.class) @RequestBody CommentDto.CreateRequest request
    ) {
        CommentDto.Response response = commentService.createComment(request);
        log.info("POST /api/v1/comments - Comment created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a comment",
            description = "Partially updates an existing comment's content."
    )
    public ResponseEntity<CommentDto.Response> update(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody CommentDto.UpdateRequest request
    ) {
        CommentDto.Response response = commentService.updateComment(id, request);
        log.info("PATCH /api/v1/comments/{} - Comment updated", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a comment",
            description = "Deletes a comment by id."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.deleteComment(id);
        log.info("DELETE /api/v1/comments/{} - Comment deleted", id);
        return ResponseEntity.noContent().build();
    }
}