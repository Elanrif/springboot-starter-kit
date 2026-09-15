package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import org.springframework.data.jpa.domain.Specification;

public final class CommentSpecification {

    private CommentSpecification() {
    }

    public static Specification<Comment> from(CommentDto.Filter filter) {
        if (filter == null) {
            return excludeSoftDeleted();
        }

        return Specification.allOf(
                excludeSoftDeleted(),
                hasAuthor(filter.authorId()),
                hasPost(filter.postId()),
                search(filter.search())
        );
    }

    private static Specification<Comment> excludeSoftDeleted() {
        return Specification.allOf(
                excludeSoftDeleteAuthor(),
                excludeSoftDeletePostAuthor()
        );
    }

    private static Specification<Comment> excludeSoftDeleteAuthor() {
        return (root, query, cb) -> cb.and(
                cb.isNull(root.get("author").get("deletedAt")),
                cb.notEqual(root.get("author").get("status"), UserStatus.DELETED)
        );
    }

    private static Specification<Comment> excludeSoftDeletePostAuthor() {
        return (root, query, cb) -> cb.and(
                cb.isNull(root.get("post").get("author").get("deletedAt")),
                cb.notEqual(root.get("post").get("author").get("status"), UserStatus.DELETED)
        );
    }

    private static Specification<Comment> hasAuthor(Long authorId) {
        if (authorId == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("author").get("id"),
                        authorId
                );
    }

    private static Specification<Comment> hasPost(Long postId) {
        if (postId == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("post").get("id"),
                        postId
                );
    }

    private static Specification<Comment> search(String search) {
        if (search == null || search.isBlank()) {
            return Specification.unrestricted();
        }

        String like = "%" + search.toLowerCase() + "%";

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("content")),
                        like
                );
    }

}