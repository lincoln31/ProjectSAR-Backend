package com.conjuntoresidencial.api.infrastructure.web.dto.response;

import lombok.Data;

@Data
public class ApartmentResponseDto {
    private Long id;
    private String number;

    private String status;
    private String description;
    private Long towerId;
    private String towerName; // Para dar más contexto
}