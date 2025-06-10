package com.conjuntoresidencial.api.infrastructure.web.dto.response;

import com.conjuntoresidencial.api.domain.transaction.model.TransactionType;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponseDto {
    private Long id;
    private TransactionType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;

    private Long relatedPaymentId;
    // Información del usuario de la transacción (si aplica)
    private Long userId;
    private String userName; // Nombre completo o username
    // Información del apartamento (si aplica)
    private Long apartmentId;
    private String apartmentNumber;
    private String towerName; // Del apartamento
    // Información de quién creó la transacción
    private Long createdById;
    private String createdByName;
}