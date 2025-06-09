package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.conjuntoresidencial.api.domain.residency.model.ResidencyType;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.residency.port.out.ResidencyRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.ResidencyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ResidencyPersistenceAdapter implements ResidencyRepositoryPort {
    private final ResidencyJpaRepository residencyJpaRepository;

    @Override public Residency save(Residency residency) { return residencyJpaRepository.save(residency); }
    @Override public Optional<Residency> findById(Long id) { return residencyJpaRepository.findById(id); }
    @Override public List<Residency> findByUser(User user) { return residencyJpaRepository.findByUser(user); }
    @Override public List<Residency> findByApartment(Apartment apartment) { return residencyJpaRepository.findByApartment(apartment); }
    @Override public Optional<Residency> findByUserAndApartmentAndResidencyType(User user, Apartment apartment, ResidencyType residencyType) { return residencyJpaRepository.findByUserAndApartmentAndResidencyType(user, apartment, residencyType); }
    @Override public List<Residency> findAll() { return residencyJpaRepository.findAll(); }
    @Override public void deleteById(Long id) { residencyJpaRepository.deleteById(id); }
    @Override public boolean existsByUserAndApartmentAndResidencyType(User user, Apartment apartment, ResidencyType residencyType) { return residencyJpaRepository.existsByUserAndApartmentAndResidencyType(user, apartment, residencyType); }
}