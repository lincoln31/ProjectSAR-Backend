package com.conjuntoresidencial.api.domain.residency.model;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp; // Para la fecha de creación

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "residencies", uniqueConstraints = {
        // Para la versión más simple, un usuario solo puede tener una vinculación activa a un apartamento.
        // Si permitimos múltiples (ej. propietario e inquilino a la vez), quitaríamos este constraint
        // o lo haríamos más complejo (ej. user_id, apartment_id, type deben ser únicos si queremos un tipo por usuario/apto)
        // Por ahora, lo más simple: un usuario no puede ser dos veces lo mismo en el mismo apto.
        @UniqueConstraint(columnNames = {"user_id", "apartment_id", "residency_type"})
})
public class Residency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false)
    private Apartment apartment;

    @Enumerated(EnumType.STRING) // Guarda el nombre del enum como String en la BD
    @Column(name = "residency_type", nullable = false, length = 50)
    private ResidencyType residencyType;

    // Opcional para la versión sencilla, pero útil
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Fecha en que se creó la vinculación

    // Si quisiéramos fechas de inicio/fin:
    // private LocalDate startDate;
    // private LocalDate endDate;
    // private boolean isActive;
}