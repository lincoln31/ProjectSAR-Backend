package com.conjuntoresidencial.api.domain.property.port.out;

import com.conjuntoresidencial.api.domain.property.model.Tower;
import java.util.List;
import java.util.Optional;

public interface TowerRepositoryPort {
    Tower save(Tower tower);
    Optional<Tower> findById(Long id);
    Optional<Tower> findByName(String name); // Útil para evitar duplicados
    List<Tower> findAll();
    void deleteById(Long id);
    boolean existsByName(String name);
}