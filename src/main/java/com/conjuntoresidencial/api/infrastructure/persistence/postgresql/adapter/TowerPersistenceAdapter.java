package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;
import com.conjuntoresidencial.api.domain.property.model.Tower;
import com.conjuntoresidencial.api.domain.property.port.out.TowerRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.TowerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TowerPersistenceAdapter implements TowerRepositoryPort {
    private final TowerJpaRepository towerJpaRepository;

    @Override public Tower save(Tower tower) { return towerJpaRepository.save(tower); }
    @Override public Optional<Tower> findById(Long id) { return towerJpaRepository.findById(id); }
    @Override public Optional<Tower> findByName(String name) { return towerJpaRepository.findByName(name); }
    @Override public List<Tower> findAll() { return towerJpaRepository.findAll(); }
    @Override public void deleteById(Long id) { towerJpaRepository.deleteById(id); }
    @Override public boolean existsByName(String name) { return towerJpaRepository.existsByName(name); }
}