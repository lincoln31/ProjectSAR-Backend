package com.conjuntoresidencial.api.infrastructure.web.mapper;
import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.user.model.User; // Necesario para acceder a user.userProfile
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.PaymentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.PaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring") // No necesitamos 'uses' aquí si hacemos el mapeo de nombres manualmente o con @Named
public interface PaymentMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "apartment", ignore = true)
    @Mapping(target = "registeredBy", ignore = true)
    Payment toDomain(PaymentRequestDto dto);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "userToUserName") // Usar método calificado
    @Mapping(source = "apartment.id", target = "apartmentId")
    @Mapping(source = "apartment.number", target = "apartmentNumber")
    @Mapping(source = "apartment.tower.name", target = "towerName")
    @Mapping(source = "registeredBy.id", target = "registeredById")
    @Mapping(source = "registeredBy", target = "registeredByName", qualifiedByName = "userToUserName") // Reutilizar método
    PaymentResponseDto toDto(Payment payment);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "apartment", ignore = true)
    @Mapping(target = "registeredBy", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateDomainFromDto(PaymentRequestDto dto, @MappingTarget Payment payment);

    // Método calificado para obtener el nombre completo del usuario
    @Named("userToUserName")
    default String userToUserName(User user) {
        if (user == null || user.getUserProfile() == null) {
            return null; // O un string vacío, o el username si UserProfile no existe
        }
        UserProfile profile = user.getUserProfile();
        String firstName = profile.getFirstName() != null ? profile.getFirstName() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName() : "";

        // Evitar " null" o "null "
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            return firstName + " " + lastName;
        } else if (!firstName.isEmpty()) {
            return firstName;
        } else if (!lastName.isEmpty()) {
            return lastName;
        } else {
            // Si no hay nombre ni apellido en el perfil, quizás devolver el username del User
            return user.getUsername();
        }
    }
}