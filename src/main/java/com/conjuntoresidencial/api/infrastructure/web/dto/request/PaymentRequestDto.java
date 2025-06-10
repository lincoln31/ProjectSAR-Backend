package com.conjuntoresidencial.api.infrastructure.web.dto.request;

import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentRequestDto {
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Concept cannot be blank")
    @Size(max = 255)
    private String concept;

    @NotNull(message = "Payment date cannot be null")
    private LocalDate paymentDate;

    private LocalDate dueDate;

    @NotNull(message = "Status cannot be null")
    private PaymentStatus status;

    @Size(max = 50)
    private String paymentMethod;

    @Size(max = 100)
    private String referenceNumber;

    private Long userId; // Opcional, ID del usuario que paga

    @NotNull(message = "Apartment ID cannot be null")
    private Long apartmentId;

    // No incluimos registeredById aquí, se tomará del usuario autenticado en el servicio
}