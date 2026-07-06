package com.elanrif.springbootstarterkit.specification;

import com.elanrif.springbootstarterkit.entity.Address;
import org.springframework.data.jpa.domain.Specification;


public class AddressSpecification {

    public static Specification<Address> hasUserId(Long userId) {
        return (root, query, cb) ->
                cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Address> hasCountry(String country) {
        return (root, query, cb) ->
                country == null || country.isBlank()
                        ? null
                        : cb.equal(root.get("country"), country);
    }

    public static Specification<Address> hasCity(String city) {
        return (root, query, cb) ->
                city == null || city.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.get("city")),
                        "%" + city.toLowerCase() + "%"
                );
    }
}