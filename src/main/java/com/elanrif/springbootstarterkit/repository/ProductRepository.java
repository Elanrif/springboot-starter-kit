package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    long countByIsActiveTrue();

    long countByIsActiveFalse();

    long countByStockLessThanEqual(int stock);
}

