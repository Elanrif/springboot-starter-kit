package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.product.ProductCreateDto;
import com.elanrif.springbootstarterkit.dto.product.ProductDto;
import com.elanrif.springbootstarterkit.dto.product.ProductFilterDto;
import com.elanrif.springbootstarterkit.dto.product.ProductUpdateDto;
import com.elanrif.springbootstarterkit.entity.Category;
import com.elanrif.springbootstarterkit.entity.Product;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.ProductMapper;
import com.elanrif.springbootstarterkit.repository.CategoryRepository;
import com.elanrif.springbootstarterkit.repository.ProductRepository;
import com.elanrif.springbootstarterkit.util.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductDto> getProducts(ProductFilterDto filter, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, toSort(filter));
        Specification<Product> specification = buildSpecification(filter);
        Page<ProductDto> result = productRepository.findAll(specification, pageRequest)
                .map(productMapper::toDto);
        return PageResponse.from(result);
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return productMapper.toDto(product);
    }

    public ProductDto getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + slug));
        return productMapper.toDto(product);
    }

    public ProductDto createProduct(ProductCreateDto dto) {
        Product product = productMapper.toEntity(dto);
        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.categoryId()));
            product.setCategory(category);
        }
        return productMapper.toDto(productRepository.save(product));
    }

    public ProductDto updateProduct(Long id, ProductUpdateDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        productMapper.updateFromDto(dto, product);
        if (dto.categoryId() != null) {
            Category category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + dto.categoryId()));
            product.setCategory(category);
        }
        return productMapper.toDto(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }


    private Sort toSort(ProductFilterDto filter) {
        if (filter == null || filter.sortBy() == null || filter.sortBy().isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String sortBy = filter.sortBy();
        Sort.Direction direction = sortBy.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String property = sortBy.startsWith("-") ? sortBy.substring(1) : sortBy;
        return Sort.by(direction, property);
    }

    private Specification<Product> buildSpecification(ProductFilterDto filter) {
        if (filter == null) {
            return Specification.where((Specification<Product>) null);
        }
        return Specification.where(search(filter.search()))
                .and(category(filter.categoryId()))
                .and(isActive(filter.isActive()));
    }

    private Specification<Product> search(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return null;
            }
            String like = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("slug")), like)
            );
        };
    }

    private Specification<Product> category(Long categoryId) {
        return (root, query, cb) -> categoryId == null
                ? null
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Product> isActive(Boolean active) {
        return (root, query, cb) -> active == null ? null : cb.equal(root.get("isActive"), active);
    }
}

