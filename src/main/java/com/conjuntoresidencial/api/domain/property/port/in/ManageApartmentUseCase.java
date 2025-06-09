package com.conjuntoresidencial.api.domain.property.port.in;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ApartmentRequestDto;
import java.util.List;

public interface ManageApartmentUseCase {
    Apartment createApartment(ApartmentRequestDto apartmentDto);
    Apartment getApartmentById(Long id);
    List<Apartment> getAllApartments();
    List<Apartment> getApartmentsByTowerId(Long towerId);
    Apartment updateApartment(Long id, ApartmentRequestDto apartmentDto);
    void deleteApartment(Long id);
}