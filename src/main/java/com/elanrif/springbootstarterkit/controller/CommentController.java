package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.services.CommentService;
import com.elanrif.springbootstarterkit.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<PageResponse<CommentDto.Response>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/comments - Fetching comments with search: {}, postId: {}, authorId: {}, page: {}, size: {}",
                search, postId, authorId, page, size);
        CommentDto.Filter filter = new CommentDto.Filter(search, postId, authorId, sortBy);
        PageResponse<CommentDto.Response> response = commentService.getComments(filter, page, size);
        log.info("Returned {} comments (total: {})", response.content().size(), response.totalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto.Response> getById(@PathVariable Long id) {
        log.info("GET /api/v1/comments/{} - Fetching comment by id", id);
        CommentDto.Response response = commentService.getCommentById(id);
        log.info("Returned comment with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CommentDto.Response> create(@Valid @RequestBody CommentDto.CreateRequest request) {
        log.info("POST /api/v1/comments - Creating comment for postId: {}, authorId: {}", request.postId(), request.authorId());
        CommentDto.Response response = commentService.createComment(request);
        log.info("Comment created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommentDto.Response> update(@PathVariable Long id, @Valid @RequestBody CommentDto.UpdateRequest request) {
        log.info("PATCH /api/v1/comments/{} - Updating comment", id);
        CommentDto.Response response = commentService.updateComment(id, request);
        log.info("Comment updated with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/comments/{} - Deleting comment", id);
        commentService.deleteComment(id);
        log.info("Comment deleted with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
