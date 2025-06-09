package com.conjuntoresidencial.api.infrastructure.web.dto.response;


import com.conjuntoresidencial.api.domain.residency.model.ResidencyType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResidencyResponseDto {
    private Long id;
    private Long userId;
    private String username; // Para mostrar info del usuario
    private String userFullName; // firstName + lastName
    private Long apartmentId;
    private String apartmentNumber; // Para mostrar info del apartamento
    private String towerName; // Para mostrar info de la torre
    private ResidencyType residencyType;
    private LocalDateTime createdAt;
}