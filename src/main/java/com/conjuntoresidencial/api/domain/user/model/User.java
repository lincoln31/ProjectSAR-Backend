package com.conjuntoresidencial.api.domain.user.model;

import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = {"userProfile", "roles", "residencies"}) // Asume que podrías tener 'residencies'
@EqualsAndHashCode(exclude = {"userProfile", "roles", "residencies"})
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; // Se almacenará hasheada

    @Column(nullable = false, unique = true, length = 100)
    private String email;





    private boolean enabled = true; // Para activar/desactivar usuarios

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default // Asegura que el Set se inicialice si no se provee en el builder
    private Set<Role> roles = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación con UserProfile (dueña de la relación por 'mappedBy')
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference("user-profile")
    private UserProfile userProfile;
    // En User.java
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY) // Asumiendo que Residency tiene un campo "user"
    private Set<Residency> residencies = new HashSet<>();
}