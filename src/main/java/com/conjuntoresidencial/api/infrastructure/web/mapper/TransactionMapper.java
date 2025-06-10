package com.conjuntoresidencial.api.infrastructure.web.mapper;

import com.conjuntoresidencial.api.domain.transaction.model.Transaction;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.model.UserProfile;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.TransactionResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "relatedPayment.id", target = "relatedPaymentId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "userName", qualifiedByName = "userToTransactionUserName")
    @Mapping(source = "apartment.id", target = "apartmentId")
    @Mapping(source = "apartment.number", target = "apartmentNumber")
    @Mapping(source = "apartment.tower.name", target = "towerName")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy", target = "createdByName", qualifiedByName = "userToTransactionUserName")
    TransactionResponseDto toDto(Transaction transaction);

    @Named("userToTransactionUserName")
    default String userToTransactionUserName(User user) {
        if (user == null) return null;
        // Reutilizar lógica similar a PaymentMapper si es necesario
        // o simplemente devolver el username si el perfil no es relevante aquí
        // Por simplicidad, usemos el username
        return user.getUsername();
        /*
        // O si necesitas el nombre completo del perfil:
        if (user.getUserProfile() == null) return user.getUsername();
        UserProfile profile = user.getUserProfile();
        String firstName = profile.getFirstName() != null ? profile.getFirstName() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName() : "";
        if (!firstName.isEmpty() && !lastName.isEmpty()) return firstName + " " + lastName;
        if (!firstName.isEmpty()) return firstName;
        if (!lastName.isEmpty()) return lastName;
        return user.getUsername();
        */
    }
}