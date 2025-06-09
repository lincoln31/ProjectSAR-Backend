package com.conjuntoresidencial.api.application.property.service;

import com.conjuntoresidencial.api.domain.property.model.Tower;
import com.conjuntoresidencial.api.domain.property.port.in.ManageTowerUseCase; // Crear esta interfaz
import com.conjuntoresidencial.api.domain.property.port.out.TowerRepositoryPort;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.TowerRequestDto; // Usaremos DTOs aquí
import com.conjuntoresidencial.api.infrastructure.web.mapper.TowerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TowerManagementService implements ManageTowerUseCase { // Implementar la interfaz
    private final TowerRepositoryPort towerRepository;
    private final TowerMapper towerMapper;

    @Override
    @Transactional
    public Tower createTower(TowerRequestDto towerDto) {
        if (towerRepository.existsByName(towerDto.getName())) {
            throw new IllegalArgumentException("Tower with name '" + towerDto.getName() + "' already exists.");
        }
        Tower tower = towerMapper.toDomain(towerDto);
        return towerRepository.save(tower);
    }

    @Override
    @Transactional(readOnly = true)
    public Tower getTowerById(Long id) {
        return towerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tower> getAllTowers() {
        return towerRepository.findAll();
    }

    @Override
    @Transactional
    public Tower updateTower(Long id, TowerRequestDto towerDto) {
        Tower existingTower = getTowerById(id); // Reusa el método para obtener y lanzar NotFound
        // Verificar si el nuevo nombre ya existe en otra torre (opcional, pero buena práctica)
        towerRepository.findByName(towerDto.getName()).ifPresent(t -> {
            if (!t.getId().equals(existingTower.getId())) {
                throw new IllegalArgumentException("Another tower with name '" + towerDto.getName() + "' already exists.");
            }
        });
        towerMapper.updateDomainFromDto(towerDto, existingTower);
        return towerRepository.save(existingTower);
    }

    @Override
    @Transactional
    public void deleteTower(Long id) {
        if (!towerRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Tower not found with id: " + id);
        }
        towerRepository.deleteById(id);
    }
}