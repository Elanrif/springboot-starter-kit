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

    /*
     * @ManyToOne is EAGER by default; we use LAZY to avoid unnecessary loading.
     * LAZY controls database fetching only, not JSON recursion.
     * @JsonIgnore prevents recursive serialization with Jackson.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /*
     * @ManyToOne is EAGER by default; we use LAZY to avoid unnecessary loading.
     * LAZY controls database fetching only, not JSON recursion.
     * @JsonIgnore prevents recursive serialization with Jackson.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
