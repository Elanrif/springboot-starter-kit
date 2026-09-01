package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @Query("""
        SELECT DISTINCT p FROM Post p
        JOIN FETCH p.author
        LEFT JOIN FETCH p.comments c
        LEFT JOIN FETCH c.author
        WHERE p.id = :id
        """)
    Optional<Post> findByIdWithDetails(@Param("id") Long id);
}
