package com.elanrif.springbootstarterkit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@Table(name = "categories")
public class Category extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, unique = true)
    private String slug;

    @Size(max = 2000)
    private String description;

    @Size(max = 255)
    private String imageUrl;

    /*
     * @OneToMany is FetchTypeLAZY by default for better performance in production.
     * Why does LazyInitializationException occur when returning Category entity directly?
     *
     * - When you return a JPA entity (like Category) with a lazily loaded collection (products),
     *   and the collection is not initialized within the transaction,
     *   Spring Boot (Jackson) tries to serialize the entity to JSON for the HTTP response.
     * - If the products collection is LAZY and not fetched, accessing it during serialization
     *   triggers Hibernate to load it, but the session is already closed.
     * - This results in LazyInitializationException.
     *
     * Solution:
     * - Alternatively, use @JsonIgnore on the lazy collection to prevent serialization.
     * - Always use DTOs for API responses.
     * - Fetch all required data (including lazy collections) inside the service layer.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    @NotNull
    @Column(nullable = false)
    private Boolean isActive = true;

    @Min(0)
    private Integer sortOrder = 0;
}
