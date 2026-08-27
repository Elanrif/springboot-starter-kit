package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import org.springframework.data.jpa.domain.Specification;

public final class PostSpecification {

    private PostSpecification() {
    }

    public static Specification<Post> from(PostDto.Filter filter) {

        if (filter == null) {
            return Specification.allOf();
        }

        return Specification.allOf(
                search(filter.search()),
                author(filter.authorId())
        );
    }

    private static Specification<Post> search(String search) {
        if (search == null || search.isBlank()) {
            return null;
        }

        return (root, query, cb) -> {
            String like = "%" + search.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    private static Specification<Post> author(Long authorId) {
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