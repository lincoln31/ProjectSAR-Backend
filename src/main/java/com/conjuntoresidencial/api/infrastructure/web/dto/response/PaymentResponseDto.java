package com.conjuntoresidencial.api.infrastructure.web.dto.response;

import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentResponseDto {
    private Long id;
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
    private String apartmentNumber; // Número del apartamento
    private String towerName; // Nombre de la torre del apartamento
    private Long registeredById;
    private String registeredByName; // Nombre del admin/usuario que registró
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}