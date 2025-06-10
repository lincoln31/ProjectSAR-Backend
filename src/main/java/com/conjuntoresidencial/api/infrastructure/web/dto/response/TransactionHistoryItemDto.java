package com.conjuntoresidencial.api.infrastructure.web.dto.response;

import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionHistoryItemDto { // Similar a PaymentResponseDto
    private Long id; // ID del pago original
    private BigDecimal amount;
    private String concept;
    private LocalDate paymentDate;
    private LocalDate dueDate;
    private PaymentStatus status;
    private String paymentMethod;
    private String referenceNumber;

    private Long userId;
    private String userName; // Nombre del usuario que pagó (si aplica)

    private Long apartmentId;
    private String apartmentNumber;
    private String towerName;

    private Long registeredById;
    private String registeredByName; // Nombre de quien registró

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}