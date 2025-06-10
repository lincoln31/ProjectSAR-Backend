package com.conjuntoresidencial.api.domain.transaction.model;

import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionType type; // Tipo de transacción

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // Monto de la transacción (puede ser positivo o negativo para reembolsos/ajustes)

    @Column(nullable = false, length = 255)
    private String description; // Descripción de la transacción

    // Referencia al Payment que originó esta transacción (si aplica)
    // Puede ser null si la transacción no está directamente ligada a un Payment específico
    // (ej. un ajuste manual de saldo).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment relatedPayment;

    // Usuario al que se le atribuye la transacción (si aplica, ej. quién pagó, a quién se le ajustó)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Apartamento relacionado con la transacción (si aplica)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apartment_id")
    private Apartment apartment;

    // Quién registró esta transacción en el sistema (ej. un administrador, o el sistema automáticamente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "transaction_date", nullable = false, updatable = false)
    private LocalDateTime transactionDate; // Fecha y hora en que se registró la transacción
}