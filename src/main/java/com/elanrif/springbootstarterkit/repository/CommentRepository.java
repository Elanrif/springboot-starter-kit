package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.Comment;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    @Override
    @Query("""
        SELECT c
        FROM Comment c
        JOIN FETCH c.author
        JOIN FETCH c.post
        WHERE c.id = :id
        """)
    Optional<Comment> findById(@NonNull @Param("id") Long id);

    @Modifying
    @Query(value = "DELETE FROM comments WHERE author_id = :userId", nativeQuery = true)
    void deleteByAuthorId(@Param("userId") Long userId);
}
