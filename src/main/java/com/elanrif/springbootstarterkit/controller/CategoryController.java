package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.category.CategoryCreateDto;
import com.elanrif.springbootstarterkit.dto.category.CategoryDto;
import com.elanrif.springbootstarterkit.dto.category.CategoryProductDto;
import com.elanrif.springbootstarterkit.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * List all categories with their products (by default).
     */
    @GetMapping
    public List<CategoryDto> list() {
        return categoryService.getAll();
    }

    /**
     * Get a category by id. By default, products are included (withProducts=true).
     * Set withProducts=false to exclude products from the response.
     *
     * Example: /api/v1/categories/1?withProducts=false
     */
    @GetMapping("/{id}")
    public CategoryDto getById(
            @PathVariable Long id
    ) {
        return categoryService.getById(id);
    }

    @GetMapping("/{id}/products")
    public CategoryProductDto getByIdWithProducts(
            @PathVariable Long id
    ) {
        return categoryService.getByIdWithProducts(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody CategoryCreateDto dto) {
        return categoryService.create(dto);
    }

    @PatchMapping("/{id}")
    public CategoryDto update(@PathVariable Long id, @Valid @RequestBody CategoryCreateDto dto) {
        return categoryService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
