package com.conjuntoresidencial.api.domain.payment.model;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2) // Ej: 10 dígitos en total, 2 decimales
    private BigDecimal amount; // Monto del pago

    @Column(nullable = false, length = 255)
    private String concept; // Concepto del pago (Ej: "Administración Mayo 2024", "Alquiler Junio 2024")

    @Column(nullable = false)
    private LocalDate paymentDate; // Fecha en que se realizó o se registró el pago

    private LocalDate dueDate; // Fecha de vencimiento del pago (opcional)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 50)
    private String paymentMethod; // Método de pago (Ej: "Efectivo", "Transferencia", "PSE_SIMULADO")

    @Column(length = 100)
    private String referenceNumber; // Número de referencia de la transacción (opcional)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // Quién realizó/es responsable del pago (puede ser null si el pago es solo del apto)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id", nullable = false) // Apartamento al que corresponde el pago
    private Apartment apartment;

    // Quién registró el pago en el sistema (ej. un administrador)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by_user_id")
    private User registeredBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}