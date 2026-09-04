package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.PaginationDto;
import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.services.PostService;
import com.elanrif.springbootstarterkit.dto.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Tag(
        name = "Posts",
        description = "Create, read, update and delete blog posts"
)
public class PostController {

    private final PostService postService;

    @GetMapping
    @Operation(
            summary = "List posts",
            description = "Returns a paginated list of posts, optionally filtered."
    )
    public ResponseEntity<PageResponse<PostDto.Response>> list(
            @ModelAttribute PostDto.Filter filter,
            @ModelAttribute PaginationDto.Pagination pagination
    ) {
        log.debug("GET /api/v1/posts - page: {}, size: {}", pagination.page(), pagination.size());
        PageResponse<PostDto.Response> response = postService.getPosts(filter, pagination);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a post by id",
            description = "Returns a single post. Returns 404 if it does not exist."
    )
    public ResponseEntity<PostDto.Response> getById(@PathVariable Long id) {
        log.debug("GET /api/v1/posts/{}", id);
        PostDto.Response response = postService.getPostById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(
            summary = "Create a post",
            description = "Creates a new post on behalf of the given author."
    )
    public ResponseEntity<PostDto.Response> create(@Valid @RequestBody PostDto.CreateRequest request) {
        PostDto.Response response = postService.createPost(request);
        log.info("POST /api/v1/posts - Post created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a post",
            description = "Partially updates an existing post."
    )
    public ResponseEntity<PostDto.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody PostDto.UpdateRequest request
    ) {
        PostDto.Response response = postService.updatePost(id, request);
        log.info("PATCH /api/v1/posts/{} - Post updated", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a post",
            description = "Deletes a post by id."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        log.info("DELETE /api/v1/posts/{} - Post deleted", id);
        return ResponseEntity.noContent().build();
    }
}