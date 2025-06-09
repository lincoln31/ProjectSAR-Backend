package com.conjuntoresidencial.api.infrastructure.web.mapper;

import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.UserProfileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // Para que Spring lo detecte como un bean
public interface UserProfileMapper {

    UserProfileMapper INSTANCE = Mappers.getMapper(UserProfileMapper.class);

    @Mapping(source = "user.id", target = "userId")
        // @Mapping(source = "user.username", target = "username") // Si quieres incluirlo
    UserProfileResponseDto toDto(UserProfile userProfile);

    // Podrías tener un método para toDomain si el DTO de request es muy similar
    // UserProfile toDomain(UserProfileRequestDto dto);
}