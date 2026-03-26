package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.CategoryDto;
import com.elanrif.springbootstarterkit.entity.Category;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.CategoryMapper;
import com.elanrif.springbootstarterkit.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto.Response> getAll() {
        return categoryRepository.findAllWithProducts().stream()
                .map(categoryMapper::toResponse)
                .toList();
    }

    public CategoryDto.Response getById(Long id) {
         Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
         return categoryMapper.toResponse(category);
    }

    public CategoryDto.DetailResponse getByIdWithProducts(Long id) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        return categoryMapper.toDetailResponse(category);
    }

    public CategoryDto.Response create(CategoryDto.CreateRequest request) {
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public CategoryDto.Response update(Long id, CategoryDto.UpdateRequest request) {
        Category category = categoryRepository.findByIdWithProducts(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        categoryMapper.updateFromRequest(request, category);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
