package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.CategoryDto;
import com.elanrif.springbootstarterkit.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDto.Response>> list() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping("/{id}/products")
    public ResponseEntity<CategoryDto.DetailResponse> getByIdWithProducts(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getByIdWithProducts(id));
    }

    @PostMapping
    public ResponseEntity<CategoryDto.Response> create(@Valid @RequestBody CategoryDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryDto.Response> update(@PathVariable Long id, @Valid @RequestBody CategoryDto.UpdateRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
