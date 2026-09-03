package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.Post;
import lombok.NonNull;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    @Override
    @EntityGraph(attributePaths = {
            "author",
            "comments",
            "comments.author"
    })
    Optional<Post> findById(@NonNull Long id);

    @Modifying
    @Query(value = "DELETE FROM posts WHERE author_id = :userId", nativeQuery = true)
    void deleteByAuthorId(@Param("userId") Long userId);
}
