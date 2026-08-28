package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.CommentDto;
import com.elanrif.springbootstarterkit.entity.Comment;
import org.springframework.data.jpa.domain.Specification;

public final class CommentSpecification {

    private CommentSpecification() {
    }

    public static Specification<Comment> from(
            CommentDto.Filter filter
    ) {
        return Specification.allOf(
                author(filter != null ? filter.authorId() : null),
                post(filter != null ? filter.postId() : null),
                search(filter != null ? filter.search() : null)
        );
    }

    private static Specification<Comment> author(Long authorId) {
        if (authorId == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("author").get("id"),
                        authorId
                );
    }

    private static Specification<Comment> post(Long postId) {
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