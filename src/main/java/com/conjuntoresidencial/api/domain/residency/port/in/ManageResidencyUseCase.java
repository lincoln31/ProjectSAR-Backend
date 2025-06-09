package com.conjuntoresidencial.api.domain.residency.port.in;

import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ResidencyRequestDto;
import java.util.List;

public interface ManageResidencyUseCase {
    Residency createResidency(ResidencyRequestDto residencyDto);
    List<Residency> getResidenciesByUserId(Long userId);
    List<Residency> getResidenciesByApartmentId(Long apartmentId);
    Residency getResidencyById(Long id); // Opcional, si se necesita GET por ID de Residency
    void deleteResidency(Long id);
}