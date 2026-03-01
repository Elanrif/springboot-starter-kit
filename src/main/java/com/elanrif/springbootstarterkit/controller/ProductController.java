package com.elanrif.springbootstarterkit.controller;

import com.elanrif.springbootstarterkit.dto.product.ProductCreateDto;
import com.elanrif.springbootstarterkit.dto.product.ProductDto;
import com.elanrif.springbootstarterkit.dto.product.ProductFilterDto;
import com.elanrif.springbootstarterkit.dto.product.ProductUpdateDto;
import com.elanrif.springbootstarterkit.services.ProductService;
import com.elanrif.springbootstarterkit.util.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PageResponse<ProductDto> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ProductFilterDto filter = new ProductFilterDto(search, categoryId, isActive, sortBy);
        return productService.getProducts(filter, page, size);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/slug/{slug}")
    public ProductDto getBySlug(@PathVariable String slug) {
        return productService.getProductBySlug(slug);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody ProductCreateDto dto) {
        return productService.createProduct(dto);
    }

    @PatchMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDto dto) {
        return productService.updateProduct(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

}

