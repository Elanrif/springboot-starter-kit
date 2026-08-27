package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.dto.AddressDto;
import com.elanrif.springbootstarterkit.entity.Address;
import org.springframework.data.jpa.domain.Specification;

public final class AddressSpecification {

    private AddressSpecification() {
    }

    public static Specification<Address> from(
            Long userId,
            AddressDto.Filter filter
    ) {
        return Specification.allOf(
                hasUserId(userId),
                hasCountry(filter != null ? filter.country() : null),
                hasCity(filter != null ? filter.city() : null)
        );
    }

    private static Specification<Address> hasUserId(Long userId) {
        if (userId == null) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(
                        root.get("user").get("id"),
                        userId
                );
    }

    private static Specification<Address> hasCountry(String country) {
        if (country == null || country.isBlank()) {
            return Specification.unrestricted();
        }

        return (root, query, cb) ->
                cb.equal(
                        cb.lower(root.get("country")),
                        country.toLowerCase()
                );
    }

    private static Specification<Address> hasCity(String city) {
        if (city == null || city.isBlank()) {
            return Specification.unrestricted();
        }

        String like = "%" + city.toLowerCase() + "%";

        return (root, query, cb) ->
                cb.like(
                        cb.lower(root.get("city")),
                        like
                );
    }
}