package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.PostDto;
import com.elanrif.springbootstarterkit.entity.Post;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import org.springframework.data.jpa.domain.Specification;

public final class PostSpecification {

    private PostSpecification() {
    }

    public static Specification<Post> from(PostDto.Filter filter) {
        if (filter == null) {
            return excludeDeletedAuthor();
        }

        return Specification.allOf(
                excludeDeletedAuthor(),
                hasAuthor(filter.authorId()),
                search(filter.search())
        );
    }

    private static Specification<Post> excludeDeletedAuthor() {
        return (root, query, cb) -> cb.and(
                cb.isNull(root.get("author").get("deletedAt")),
                cb.notEqual(root.get("author").get("status"), UserStatus.DELETED)
        );
    }

    private static Specification<Post> hasAuthor(Long authorId) {
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