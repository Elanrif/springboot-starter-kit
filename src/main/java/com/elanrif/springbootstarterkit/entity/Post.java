package com.elanrif.springbootstarterkit.entity;

import com.elanrif.springbootstarterkit.entity.Comment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
@Table(name = "posts")
public class Post extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = true, unique = true)
    private String imageUrl;

    @Size(max = 2000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @PositiveOrZero
    @Column(nullable = true)
    private Long likes = 0L;

    /*
     * @ManyToOne is EAGER by default; we use LAZY to avoid unnecessary loading.
     * LAZY controls database fetching only, not JSON recursion.
     * @JsonIgnore prevents recursive serialization with Jackson.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /*
     * `mappedBy` marks this side as the inverse side of the relationship.
     * The owning side (the one with the foreign key) is defined in the other entity.
     * We keep this association LAZY and expose DTOs from the service layer
     * to avoid LazyInitializationException and recursive JSON issues.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

}
