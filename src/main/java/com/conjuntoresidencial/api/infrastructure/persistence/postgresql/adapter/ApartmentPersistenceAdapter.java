package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;
import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.model.Tower;
import com.conjuntoresidencial.api.domain.property.port.out.ApartmentRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.ApartmentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApartmentPersistenceAdapter implements ApartmentRepositoryPort {
    private final ApartmentJpaRepository apartmentJpaRepository;

    @Override public Apartment save(Apartment apartment) { return apartmentJpaRepository.save(apartment); }
    @Override public Optional<Apartment> findById(Long id) { return apartmentJpaRepository.findById(id); }
    @Override public List<Apartment> findAll() { return apartmentJpaRepository.findAll(); }
    @Override public List<Apartment> findByTower(Tower tower) { return apartmentJpaRepository.findByTower(tower); }
    @Override public Optional<Apartment> findByNumberAndTower(String number, Tower tower) { return apartmentJpaRepository.findByNumberAndTower(number, tower); }
    @Override public void deleteById(Long id) { apartmentJpaRepository.deleteById(id); }
}