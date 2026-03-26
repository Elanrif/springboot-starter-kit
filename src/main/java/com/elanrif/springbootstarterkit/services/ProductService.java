package com.elanrif.springbootstarterkit.services;

import com.elanrif.springbootstarterkit.dto.ProductDto;
import com.elanrif.springbootstarterkit.entity.Category;
import com.elanrif.springbootstarterkit.entity.Product;
import com.elanrif.springbootstarterkit.exception.ResourceNotFoundException;
import com.elanrif.springbootstarterkit.mapper.ProductMapper;
import com.elanrif.springbootstarterkit.repository.CategoryRepository;
import com.elanrif.springbootstarterkit.repository.ProductRepository;
import com.elanrif.springbootstarterkit.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public PageResponse<ProductDto.Response> getProducts(ProductDto.Filter filter, int page, int size) {
        log.debug("Fetching products with filter - page: {}, size: {}, search: {}, categoryId: {}",
                page, size, filter != null ? filter.search() : null, filter != null ? filter.categoryId() : null);
        PageRequest pageRequest = PageRequest.of(page, size, toSort(filter));
        Specification<Product> specification = buildSpecification(filter);
        Page<ProductDto.Response> result = productRepository.findAll(specification, pageRequest)
                .map(productMapper::toResponse);
        log.debug("Found {} products (total: {})", result.getNumberOfElements(), result.getTotalElements());
        return PageResponse.from(result);
    }

    public ProductDto.Response getProductById(Long id) {
        log.debug("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found: " + id);
                });
        return productMapper.toResponse(product);
    }

    public ProductDto.Response getProductBySlug(String slug) {
        log.debug("Fetching product with slug: {}", slug);
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    log.warn("Product not found with slug: {}", slug);
                    return new ResourceNotFoundException("Product not found: " + slug);
                });
        return productMapper.toResponse(product);
    }

    public ProductDto.Response createProduct(ProductDto.CreateRequest request) {
        log.debug("Creating product with name: {}", request.name());
        Product product = productMapper.toEntity(request);
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> {
                        log.warn("Category not found with id: {}", request.categoryId());
                        return new ResourceNotFoundException("Category not found: " + request.categoryId());
                    });
            product.setCategory(category);
        }
        ProductDto.Response response = productMapper.toResponse(productRepository.save(product));
        log.info("Product created successfully with id: {}", response.id());
        return response;
    }

    public ProductDto.Response updateProduct(Long id, ProductDto.UpdateRequest request) {
        log.debug("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed - product not found with id: {}", id);
                    return new ResourceNotFoundException("Product not found: " + id);
                });
        productMapper.updateFromRequest(request, product);
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> {
                        log.warn("Category not found with id: {}", request.categoryId());
                        return new ResourceNotFoundException("Category not found: " + request.categoryId());
                    });
            product.setCategory(category);
        }
        ProductDto.Response response = productMapper.toResponse(productRepository.save(product));
        log.info("Product updated successfully with id: {}", id);
        return response;
    }

    public void deleteProduct(Long id) {
        log.debug("Deleting product with id: {}", id);
        if (!productRepository.existsById(id)) {
            log.warn("Delete failed - product not found with id: {}", id);
            throw new ResourceNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
        log.info("Product deleted successfully with id: {}", id);
    }


    private Sort toSort(ProductDto.Filter filter) {
        if (filter == null || filter.sortBy() == null || filter.sortBy().isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String sortBy = filter.sortBy();
        Sort.Direction direction = sortBy.startsWith("-") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String property = sortBy.startsWith("-") ? sortBy.substring(1) : sortBy;
        return Sort.by(direction, property);
    }

    private Specification<Product> buildSpecification(ProductDto.Filter filter) {
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
