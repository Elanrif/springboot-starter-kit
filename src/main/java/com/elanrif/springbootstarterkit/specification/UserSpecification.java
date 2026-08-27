package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.UserDto;
import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserRole;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

    private UserSpecification() {
    }

    public static Specification<User> from(UserDto.Filter filter) {
        if (filter == null) {
            return Specification.unrestricted();
        }

        return Specification.allOf(
                hasRole(filter.role()),
                hasStatus(filter.status())
        );
    }

    private static Specification<User> hasRole(
            UserRole role
    ) {
        if (role == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(root.get("role"), role);
    }

    private static Specification<User> hasStatus(
            UserStatus status
    ) {
        if (status == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }
}