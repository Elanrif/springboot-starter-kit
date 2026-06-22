package com.elanrif.springbootstarterkit.repository;

import com.elanrif.springbootstarterkit.entity.User;
import com.elanrif.springbootstarterkit.entity.UserStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"addresses"})
    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"addresses"})
    Optional<User> findById(Long id);

    @Query("SELECT u FROM User u WHERE " +
            "(:email IS NULL OR LOWER(u.email) LIKE :email) AND " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE :firstName) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE :lastName) AND " +
            "(:status IS NULL OR LOWER(u.status) LIKE :status)")
    Page<User> searchUsers(@Param("email") String email,
                           @Param("firstName") String firstName,
                           @Param("lastName") String lastName,
                           @Param("status") UserStatus status,
                           Pageable pageable);
}

