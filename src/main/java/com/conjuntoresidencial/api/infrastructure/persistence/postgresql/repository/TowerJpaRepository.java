package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;


import com.conjuntoresidencial.api.domain.property.model.Tower;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TowerJpaRepository extends JpaRepository<Tower, Long> {
    Optional<Tower> findByName(String name);
    boolean existsByName(String name);
}