package com.conjuntoresidencial.api.infrastructure.web.dto.response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileResponseDto {
    private Long id; // ID del perfil
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String documentId;
    private String address;
    private LocalDate birthDate;
    private Long userId; // Para referencia
    // private String username; // Podrías añadir el username si es útil
}