package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /*
     * This method uses a JPQL query to fetch all categories along with their associated products.
     * We use @Query for more complex queries that cannot be easily derived from method names.
     * In this case, we want to fetch all categories along with their associated products in a single query to avoid the N+1 select problem.
     */
    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products")
    List<Category> findAllWithProducts();
    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Optional<Category> findByIdWithProducts(@Param("id") Long id);

}
