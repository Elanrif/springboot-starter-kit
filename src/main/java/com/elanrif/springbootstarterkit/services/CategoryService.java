package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.category.CategoryCreateDto;
import com.elanrif.springbootstarterkit.dto.category.CategoryDto;
import com.elanrif.springbootstarterkit.dto.category.CategoryProductDto;
import com.elanrif.springbootstarterkit.entity.Category;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.CategoryMapper;
import com.elanrif.springbootstarterkit.mapper.CategoryProductMapper;
import com.elanrif.springbootstarterkit.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryProductMapper categoryProductMapper;

    // Fetch all categories with their products using fetch join to avoid LazyInitializationException
    public List<CategoryDto> getAll() {
        return categoryRepository.findAllWithProducts().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    /*
     * 1) Info: Why does LazyInitializationException occur when returning Category entity directly?
     * - findById() returns a Category entity with a lazily loaded products collection (no products).
     * - categoryMapper.toDto(category) tries to access category.getProducts() to map it to DTO.
     *  This is why LazyInitializationException occurs, because the products collection is not initialized and the session is closed.
     *
     * 2) Details: Why does LazyInitializationException occur when returning Category entity directly?
     * - When you return a JPA entity (like Category) with a lazily loaded collection (products),
     *   and the collection is not initialized within the transaction,
     *   Spring Boot (Jackson) tries to serialize the entity to JSON for the HTTP response.
     * - If the products collection is LAZY and not fetched, accessing it during serialization
     *   triggers Hibernate to load it, but the session is already closed.
     * - This results in LazyInitializationException.
     *
     * Solution:
     * - Alternatively, use @JsonIgnore on the lazy collection to prevent serialization.
     * - Always use DTOs for API responses.
     * - Fetch all required data (including lazy collections) inside the service layer.
     */
    public CategoryDto getById(Long id) {
         Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
         return categoryMapper.toDto(category);
    }

    /*
    * So use this method to fetch category with products using fetch join to avoid LazyInitializationException.
     * - findByIdWithProducts() uses a JPQL query with fetch join to load the category and its products in one query.
     * - This way, the products collection is initialized and can be safely accessed in the mapper without causing LazyInitializationException.
     * - Always use DTOs for API responses to control what data is serialized and avoid exposing internal entities directly.
    */
    public CategoryProductDto getByIdWithProducts(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        return categoryProductMapper.toDto(category);
    }

    public CategoryDto create(CategoryCreateDto dto) {
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public CategoryDto update(Long id, CategoryCreateDto dto) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        categoryMapper.updateFromDto(dto, category);
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
