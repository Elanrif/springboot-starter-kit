package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

    /**
     * Pour un commentaire avec son auteur et son post
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author JOIN FETCH c.post WHERE c.id = :id")
    Optional<Comment> findByIdWithAuthorAndPost(@Param("id") Long id);

    /**
     * Pour les commentaires d'un post avec leurs auteurs (évite N+1)
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId")
    List<Comment> findAllByPostIdWithAuthor(@Param("postId") Long postId);

    /**
     * Pour tous les commentaires avec leurs auteurs
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.author")
    List<Comment> findAllWithAuthor();
}
