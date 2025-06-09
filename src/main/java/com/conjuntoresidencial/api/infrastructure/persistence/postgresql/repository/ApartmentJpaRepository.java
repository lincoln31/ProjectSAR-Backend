package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.model.Tower;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApartmentJpaRepository extends JpaRepository<Apartment, Long> {
    List<Apartment> findByTower(Tower tower);
    List<Apartment> findByTowerId(Long towerId); // Alternativa útil
    Optional<Apartment> findByNumberAndTower(String number, Tower tower);
}