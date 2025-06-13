package com.conjuntoresidencial.api.domain.visitorlog.model;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp; // Para la hora de registro del log en sí
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "visitor_logs")
public class VisitorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String visitorFullName; // Nombre completo del visitante

    @Column(length = 20)
    private String visitorPhoneNumber; // Teléfono del visitante (opcional)

    // Residente a quien visita
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_visited_user_id", nullable = false)
    private User residentVisited;

    // Apartamento que visita
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_visited_id", nullable = false)
    private Apartment apartmentVisited;

    @Column(nullable = false)
    private LocalDateTime entryTimestamp; // Fecha y hora de ingreso

    private LocalDateTime exitTimestamp; // Fecha y hora de salida (puede ser null)

    @Column(columnDefinition = "TEXT")
    private String observations; // Observaciones adicionales

    // Usuario que registró la visita (ej. guardia, administrador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_user_id", nullable = false)
    private User registeredBy;

    @CreationTimestamp // Fecha en que se creó este registro de log en la BD
    @Column(name = "log_created_at", nullable = false, updatable = false)
    private LocalDateTime logCreatedAt;
}