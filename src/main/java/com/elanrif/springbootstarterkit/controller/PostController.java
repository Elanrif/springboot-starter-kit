package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.services.PostService;
import com.elanrif.springbootstarterkit.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        PostDto.Filter filter = new PostDto.Filter(search, authorId, sortBy);
        return ResponseEntity.ok(postService.getPosts(filter, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping
    public ResponseEntity<PostDto.Response> create(@Valid @RequestBody PostDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PostDto.Response> update(@PathVariable Long id, @Valid @RequestBody PostDto.UpdateRequest request) {
        return ResponseEntity.ok(postService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}

