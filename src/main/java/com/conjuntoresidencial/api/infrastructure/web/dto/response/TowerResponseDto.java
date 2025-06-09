package com.conjuntoresidencial.api.infrastructure.web.dto.response;

import lombok.Data;
// Opcionalmente, podrías incluir una lista de IDs de apartamentos o DTOs de apartamentos resumidos aquí.
// Por simplicidad, empezamos solo con los datos de la torre.
// import java.util.List;

@Data
public class TowerResponseDto {
    private Long id;
    private String name;
    // private List<Long> apartmentIds; // Ejemplo
}