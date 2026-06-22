package com.elanrif.springbootstarterkit.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Builder
@Table(name = "addresses")
public class Address extends AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false, length = 20)
    private String postalCode;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String country;

    /**
     * ⚠️ IMPORTANT:
     * Avoid "isDefault" naming (JavaBean + Jackson + MapStruct confusion)
     * Hibernate doesn't like boolean fileds start with isXxx (confusion JavaBean).
     */
    @Column(name = "is_default", nullable = false)
    private Boolean defaultAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 👈 Lien vers l'utilisateur
}
