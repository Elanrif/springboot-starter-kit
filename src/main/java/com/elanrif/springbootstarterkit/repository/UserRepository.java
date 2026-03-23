package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByIsActive(Boolean isActive);

    @Query("SELECT u FROM User u WHERE " +
            "(:email IS NULL OR LOWER(u.email) LIKE :email) AND " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE :firstName) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE :lastName) AND " +
            "(:isActive IS NULL OR u.isActive = :isActive)")
    List<User> searchUsers(@Param("email") String email,
                           @Param("firstName") String firstName,
                           @Param("lastName") String lastName,
                           @Param("isActive") Boolean isActive);
}

