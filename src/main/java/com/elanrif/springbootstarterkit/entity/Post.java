package com.elanrif.springbootstarterkit.entity;

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

    @Size(max = 200)
    @Column(nullable = true, unique = true)
    private String imageUrl;

    @Size(max = 2000)
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long likes = 0L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Builder.Default
    @JsonIgnore
    // 🔔 CASE 1: Default relationship
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

//    @Builder.Default
//    @JsonIgnore
//    // 🔔 CASE 2: CascadeType.ALL and orphanRemoval
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Comment> comments = new ArrayList<>();
}
