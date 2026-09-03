package com.elanrif.springbootstarterkit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@Table(name = "comments")
public class Comment extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false)
    private String content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    // 🔔 CASE 1: DEFAULT relationShip
//    @JoinColumn(name = "post_id", nullable = false)
//    private Post post;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    // 🔔 CASE 2: SET NULL on delete to avoid foreign key constraint violation when a post is deleted
    @JoinColumn(name = "post_id", nullable = true, foreignKey = @ForeignKey(
            foreignKeyDefinition = "FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE SET NULL"
    ))
    private Post post;
}
