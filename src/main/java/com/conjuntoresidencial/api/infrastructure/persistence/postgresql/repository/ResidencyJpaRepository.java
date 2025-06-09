package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.conjuntoresidencial.api.domain.residency.model.ResidencyType;
import com.conjuntoresidencial.api.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResidencyJpaRepository extends JpaRepository<Residency, Long> {
    List<Residency> findByUser(User user);
    List<Residency> findByUserId(Long userId);
    List<Residency> findByApartment(Apartment apartment);
    List<Residency> findByApartmentId(Long apartmentId);
    Optional<Residency> findByUserAndApartmentAndResidencyType(User user, Apartment apartment, ResidencyType residencyType);
    boolean existsByUserAndApartmentAndResidencyType(User user, Apartment apartment, ResidencyType residencyType);
}