package com.conjuntoresidencial.api.infrastructure.web.dto.request;

import  jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitorLogRequestDto {
    @NotBlank(message = "Visitor full name cannot be blank")
    @Size(max = 200)
    private String visitorFullName;

    @Size(max = 20)
    private String visitorPhoneNumber;

    @NotNull(message = "Resident visited ID cannot be null")
    private Long residentVisitedId;

    @NotNull(message = "Apartment visited ID cannot be null")
    private Long apartmentVisitedId;

    @NotNull(message = "Entry timestamp cannot be null")
    private LocalDateTime entryTimestamp; // El frontend puede enviar la hora actual

    // exitTimestamp se manejará con un endpoint separado de "registrar salida"
    private String observations;
}