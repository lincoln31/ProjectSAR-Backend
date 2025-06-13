package com.conjuntoresidencial.api.infrastructure.web.mapper;

import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.domain.visitorlog.model.VisitorLog;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VisitorLogRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VisitorLogResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface VisitorLogMapper {

    @Mapping(target = "residentVisited", ignore = true)
    @Mapping(target = "apartmentVisited", ignore = true)
    @Mapping(target = "registeredBy", ignore = true)
    VisitorLog toDomain(VisitorLogRequestDto dto);

    @Mapping(source = "residentVisited.id", target = "residentVisitedId")
    @Mapping(source = "residentVisited", target = "residentVisitedName", qualifiedByName = "userToVisitorLogUserName")
    @Mapping(source = "apartmentVisited.id", target = "apartmentVisitedId")
    @Mapping(source = "apartmentVisited.number", target = "apartmentVisitedNumber")
    @Mapping(source = "apartmentVisited.tower.name", target = "apartmentVisitedTowerName")
    @Mapping(source = "registeredBy.id", target = "registeredById")
    @Mapping(source = "registeredBy", target = "registeredByName", qualifiedByName = "userToVisitorLogUserName")
    VisitorLogResponseDto toDto(VisitorLog visitorLog);

    @Named("userToVisitorLogUserName")
    default String userToVisitorLogUserName(User user) {
        if (user == null) return null;
        if (user.getUserProfile() != null) {
            UserProfile profile = user.getUserProfile();
            String firstName = profile.getFirstName() != null ? profile.getFirstName() : "";
            String lastName = profile.getLastName() != null ? profile.getLastName() : "";
            if (!firstName.isEmpty() && !lastName.isEmpty()) return firstName + " " + lastName;
            if (!firstName.isEmpty()) return firstName;
            if (!lastName.isEmpty()) return lastName;
        }
        return user.getUsername(); // Fallback al username
    }
}