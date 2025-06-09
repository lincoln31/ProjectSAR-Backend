package com.conjuntoresidencial.api.domain.residency.port.out;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.conjuntoresidencial.api.domain.residency.model.ResidencyType;
import com.conjuntoresidencial.api.domain.user.model.User;
import java.util.List;
import java.util.Optional;

public interface ResidencyRepositoryPort {
    Residency save(Residency residency);
    Optional<Residency> findById(Long id);
    List<Residency> findByUser(User user);
    List<Residency> findByApartment(Apartment apartment);
    Optional<Residency> findByUserAndApartmentAndResidencyType(User user, Apartment apartment, ResidencyType residencyType); // Para unicidad
    List<Residency> findAll();
    void deleteById(Long id);
    boolean existsByUserAndApartmentAndResidencyType(User user, Apartment apartment, ResidencyType residencyType);
}