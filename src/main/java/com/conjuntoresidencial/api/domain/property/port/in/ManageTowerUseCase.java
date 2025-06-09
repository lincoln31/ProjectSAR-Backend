package com.conjuntoresidencial.api.domain.property.port.in;

import com.conjuntoresidencial.api.domain.property.model.Tower;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.TowerRequestDto;
import java.util.List;

public interface ManageTowerUseCase {
    Tower createTower(TowerRequestDto towerDto);
    Tower getTowerById(Long id);
    List<Tower> getAllTowers();
    Tower updateTower(Long id, TowerRequestDto towerDto);
    void deleteTower(Long id);
}