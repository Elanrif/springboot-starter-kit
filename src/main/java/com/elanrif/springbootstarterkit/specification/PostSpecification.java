package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import org.springframework.data.jpa.domain.Specification;

public final class PostSpecification {

    private PostSpecification() {
    }

    public static Specification<Post> from(PostDto.Filter filter) {
        if (filter == null) {
            return Specification.unrestricted();
        }

        return Specification.allOf(
                author(filter.authorId()),
                search(filter.search())
        );
    }

    private static Specification<Post> author(Long authorId) {
        if (authorId == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("author").get("id"),
                        authorId
                );
    }

    private static Specification<Post> search(String search) {
        if (search == null || search.isBlank()) {
            return Specification.unrestricted();
        }

        String like = "%" + search.toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }

}