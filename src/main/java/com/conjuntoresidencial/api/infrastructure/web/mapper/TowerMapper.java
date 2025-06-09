package com.conjuntoresidencial.api.infrastructure.web.mapper;

import com.conjuntoresidencial.api.domain.property.model.Tower;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.TowerRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.TowerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TowerMapper {
    Tower toDomain(TowerRequestDto dto);
    TowerResponseDto toDto(Tower tower);
    void updateDomainFromDto(TowerRequestDto dto, @MappingTarget Tower tower);
}