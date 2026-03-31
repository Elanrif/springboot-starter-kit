package com.elanrif.springbootstarterkit.entity;

import com.elanrif.springbootstarterkit.entity.Comment;
import com.elanrif.springbootstarterkit.entity.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Table(name = "users")
public class User extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Email
    @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 255)
    @Column(nullable = true)
    private String password;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 50)
    private String phoneNumber;

    @Size(max = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive = false;

    /*
     * `mappedBy` marks this side as the inverse side of the relationship.
     * The owning side (the one with the foreign key) is defined in the other entity.
     * We keep this association LAZY and expose DTOs from the service layer
     * to avoid LazyInitializationException and recursive JSON issues.
     */
    @JsonIgnore
      @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    /*
     * `mappedBy` marks this side as the inverse side of the relationship.
     * The owning side (the one with the foreign key) is defined in the other entity.
     * We keep this association LAZY and expose DTOs from the service layer
     * to avoid LazyInitializationException and recursive JSON issues.
     */
    @JsonIgnore
      @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();
}
