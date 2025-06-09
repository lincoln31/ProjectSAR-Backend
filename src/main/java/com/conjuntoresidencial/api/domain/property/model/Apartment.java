package com.conjuntoresidencial.api.domain.property.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = {"tower"}) // EXCLUIR LA REFERENCIA AL PADRE
@EqualsAndHashCode(exclude = {"tower"}) // EXCLUIR LA REFERENCIA AL PADRE
@Table(name = "apartments", uniqueConstraints = {
        // Un apartamento es único por su número DENTRO de una torre específica
        @UniqueConstraint(columnNames = {"tower_id", "number"})
})
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String number; // Ej: "101", "PH-2A"




    @Enumerated(EnumType.STRING) // Guarda el nombre del Enum como String en la BD
    @Column(nullable = false, length = 50)
    private ApartmentStatus status;

    @Lob // Para textos más largos, si es necesario
    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;



    // Relación: Muchos Apartamentos pertenecen a una Torre
    // nullable = false: Un apartamento SIEMPRE debe pertenecer a una torre.
    // fetch = FetchType.LAZY: La torre no se carga automáticamente a menos que se acceda explícitamente.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tower_id", nullable = false)
    @JsonBackReference("tower-apartments") // Para Jackson
    private Tower tower;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // O el tipo de dato que uses para timestamps

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // O el tipo de dato que uses para timestamps
    // --- FIN DE ANOTACIONES AÑADIDAS ---
}