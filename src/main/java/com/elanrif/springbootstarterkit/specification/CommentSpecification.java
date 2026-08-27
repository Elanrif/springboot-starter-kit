package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import org.springframework.data.jpa.domain.Specification;

public final class CommentSpecification {

    private CommentSpecification() {
    }

    public static Specification<Comment> from(
            CommentDto.Filter filter,
            Long postId
    ) {
        return Specification.allOf(
                post(postId),
                search(filter != null ? filter.search() : null),
                author(filter != null ? filter.authorId() : null)
        );
    }

    private static Specification<Comment> post(Long postId) {
        if (postId == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("post").get("id"),
                        postId
                );
    }

    private static Specification<Comment> search(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }

        String like = "%" + search.toLowerCase() + "%";

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("content")),
                        like
                );
    }

    private static Specification<Comment> author(Long authorId) {
        if (authorId == null) {
            return null;
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("author").get("id"),
                        authorId
                );
    }
}