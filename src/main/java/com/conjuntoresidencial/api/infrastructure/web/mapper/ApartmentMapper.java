package com.conjuntoresidencial.api.infrastructure.web.mapper;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.model.ApartmentStatus;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ApartmentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.ApartmentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ApartmentMapper {
    // No mapeamos towerId directamente a la entidad Tower aquí, eso se maneja en el servicio.
    @Mapping(target = "tower", ignore = true)
    Apartment toDomain(ApartmentRequestDto dto);

    @Mapping(source = "tower.id", target = "towerId")
    @Mapping(source = "tower.name", target = "towerName")
    ApartmentResponseDto toDto(Apartment apartment);
    @Named("stringToApartmentStatus")
    default ApartmentStatus stringToApartmentStatus(String status) {
        if (status == null) return null;
        try {
            return ApartmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Manejar el error, quizás lanzar una excepción personalizada o devolver null
            // dependiendo de tu lógica de negocio si el string no es un enum válido.
            // Para una columna NOT NULL, esto debería fallar si no es válido.
            throw new IllegalArgumentException("Invalid status value provided: " + status);
        }}

    @Mapping(target = "tower", ignore = true)
    void updateDomainFromDto(ApartmentRequestDto dto, @MappingTarget Apartment apartment);
}