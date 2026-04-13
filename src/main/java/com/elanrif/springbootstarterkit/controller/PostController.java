package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.services.PostService;
import com.elanrif.springbootstarterkit.util.PageResponse;
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
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PageResponse<PostDto.Response>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET /api/v1/posts - Fetching posts with search: {}, authorId: {}, page: {}, size: {}",
                search, authorId, page, size);
        PostDto.Filter filter = new PostDto.Filter(search, authorId, sortBy);
        PageResponse<PostDto.Response> response = postService.getPosts(filter, page, size);
        log.info("Returned {} posts (total: {})", response.content().size(), response.totalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto.DetailResponse> getById(@PathVariable Long id) {
        log.info("GET /api/v1/posts/{} - Fetching post by id", id);
        PostDto.DetailResponse response = postService.getPostById(id);
        log.info("Returned post with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PostDto.Response> create(@Valid @RequestBody PostDto.CreateRequest request) {
        log.info("POST /api/v1/posts - Creating post with title: {}, authorId: {}", request.title(), request.authorId());
        PostDto.Response response = postService.createPost(request);
        log.info("Post created with id: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto.Response> update(@PathVariable Long id, @Valid @RequestBody PostDto.UpdateRequest request) {
        log.info("PATCH /api/v1/posts/{} - Updating post", id);
        PostDto.Response response = postService.updatePost(id, request);
        log.info("Post updated with id: {}", id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1/posts/{} - Deleting post", id);
        postService.deletePost(id);
        log.info("Post deleted with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
