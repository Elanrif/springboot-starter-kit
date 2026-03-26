package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.CategoryDto;
import com.elanrif.springbootstarterkit.entity.Category;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.CategoryMapper;
import com.elanrif.springbootstarterkit.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto.Response> getAll() {
        log.debug("Fetching all categories");
        List<CategoryDto.Response> categories = categoryRepository.findAllWithProducts().stream()
                .map(categoryMapper::toResponse)
                .toList();
        log.debug("Found {} categories", categories.size());
        return categories;
    }

    public CategoryDto.Response getById(Long id) {
        log.debug("Fetching category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found: " + id);
                });
        return categoryMapper.toResponse(category);
    }

    public CategoryDto.DetailResponse getByIdWithProducts(Long id) {
        log.debug("Fetching category with products for id: {}", id);
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> {
                    log.warn("Category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found: " + id);
                });
        return categoryMapper.toDetailResponse(category);
    }

    public CategoryDto.Response create(CategoryDto.CreateRequest request) {
        log.debug("Creating category with name: {}", request.name());
        Category category = categoryMapper.toEntity(request);
        CategoryDto.Response response = categoryMapper.toResponse(categoryRepository.save(category));
        log.info("Category created successfully with id: {}", response.id());
        return response;
    }

    public CategoryDto.Response update(Long id, CategoryDto.UpdateRequest request) {
        log.debug("Updating category with id: {}", id);
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> {
                    log.warn("Update failed - category not found with id: {}", id);
                    return new ResourceNotFoundException("Category not found: " + id);
                });
        categoryMapper.updateFromRequest(request, category);
        CategoryDto.Response response = categoryMapper.toResponse(categoryRepository.save(category));
        log.info("Category updated successfully with id: {}", id);
        return response;
    }

    public void delete(Long id) {
        log.debug("Deleting category with id: {}", id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Delete failed - category not found with id: {}", id);
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
        log.info("Category deleted successfully with id: {}", id);
    }
}
