package com.conjuntoresidencial.api.infrastructure.web.dto.request;

import com.conjuntoresidencial.api.domain.residency.model.ResidencyType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResidencyRequestDto {
    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Apartment ID cannot be null")
    private Long apartmentId;

    @NotNull(message = "Residency type cannot be null")
    private ResidencyType residencyType; // El frontend enviaría "PROPIETARIO", "INQUILINO", etc.
}