package com.conjuntoresidencial.api.infrastructure.web.mapper;

import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.ResidencyResponseDto;
// No necesitamos un toDomain desde DTO porque el servicio construirá la entidad Residency
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResidencyMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(expression = "java(residency.getUser().getUserProfile() != null ? " +
            "residency.getUser().getUserProfile().getFirstName() + \" \" + residency.getUser().getUserProfile().getLastName() : " +
            "\"\")", target = "userFullName")
    @Mapping(source = "apartment.id", target = "apartmentId")
    @Mapping(source = "apartment.number", target = "apartmentNumber")
    @Mapping(source = "apartment.tower.name", target = "towerName")
    ResidencyResponseDto toDto(Residency residency);
}