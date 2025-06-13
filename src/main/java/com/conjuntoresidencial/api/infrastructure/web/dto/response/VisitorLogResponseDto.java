package com.conjuntoresidencial.api.infrastructure.web.dto.response;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VisitorLogResponseDto {
    private Long id;
    private String visitorFullName;
    private String visitorPhoneNumber;

    private Long residentVisitedId;
    private String residentVisitedName; // Nombre completo del residente

    private Long apartmentVisitedId;
    private String apartmentVisitedNumber;
    private String apartmentVisitedTowerName;

    private LocalDateTime entryTimestamp;
    private LocalDateTime exitTimestamp;
    private String observations;

    private Long registeredById;
    private String registeredByName; // Nombre del guardia/admin

    private LocalDateTime logCreatedAt;
}