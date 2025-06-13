package com.conjuntoresidencial.api.infrastructure.web.mapper;

import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.domain.vehicle.model.Vehicle;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VehicleRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VehicleResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mapping(target = "owner", ignore = true) // El owner se asigna en el servicio
    @Mapping(target = "id", ignore = true)    // No mapear id del DTO al crear
    Vehicle toDomain(VehicleRequestDto dto);

    @Mapping(source = "owner.id", target = "ownerUserId")
    @Mapping(source = "owner", target = "ownerFullName", qualifiedByName = "userToFullName")
    @Mapping(source = "owner.userProfile.documentId", target = "ownerDocumentId") // Asume que UserProfile está cargado
    // Los campos towerName y apartmentNumber se llenarán en el servicio a través de enrichVehicleResponseDto
    @Mapping(target = "towerName", ignore = true)
    @Mapping(target = "apartmentNumber", ignore = true)
    VehicleResponseDto toDto(Vehicle vehicle);

    @Mapping(target = "owner", ignore = true) // El owner se actualiza en el servicio
    @Mapping(target = "id", ignore = true)    // No se debe actualizar el ID de la entidad
    @Mapping(target = "createdAt", ignore = true) // No se debe actualizar createdAt
    @Mapping(target = "updatedAt", ignore = true) // Hibernate maneja updatedAt
    void updateDomainFromDto(VehicleRequestDto dto, @MappingTarget Vehicle vehicle);

    @Named("userToFullName")
    default String userToFullName(User user) {
        if (user == null) {
            return null;
        }
        UserProfile profile = user.getUserProfile(); // Se asume que esto está pre-cargado por el servicio
        if (profile == null) {
            return user.getUsername(); // Fallback
        }

        String firstName = profile.getFirstName() != null ? profile.getFirstName().trim() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName().trim() : "";

        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        } else if (!firstName.isEmpty()) {
            return firstName;
        } else if (!lastName.isEmpty()) {
            return lastName;
        }
        return user.getUsername(); // Fallback final
    }
}