package com.elanrif.springbootstarterkit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@Table(name = "users")
//@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW(), status = 'DELETED', " +
        "email = CONCAT('deleted_', id, '_', email), " +
        "phone_number = CONCAT('deleted_', id, '_', phone_number) " +
        "WHERE id = ?")
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
    @Column(unique = true)
    private String phoneNumber;

    @Size(max = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    // Soft delete timestamp
    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Address> addresses = new ArrayList<>();

    @Builder.Default
    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Post> posts = new ArrayList<>();
}
