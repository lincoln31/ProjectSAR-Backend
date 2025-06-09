package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.application.property.service.TowerManagementService;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.TowerRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.TowerResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.TowerMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/towers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Todas las operaciones de torres requieren rol ADMIN
public class TowerController {
    private final TowerManagementService towerService;
    private final TowerMapper towerMapper;

    @PostMapping
    public ResponseEntity<TowerResponseDto> createTower(@Valid @RequestBody TowerRequestDto towerDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(towerMapper.toDto(towerService.createTower(towerDto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TowerResponseDto> getTowerById(@PathVariable Long id) {
        return ResponseEntity.ok(towerMapper.toDto(towerService.getTowerById(id)));
    }

    @GetMapping
    public ResponseEntity<List<TowerResponseDto>> getAllTowers() {
        List<TowerResponseDto> dtos = towerService.getAllTowers().stream()
                .map(towerMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TowerResponseDto> updateTower(@PathVariable Long id, @Valid @RequestBody TowerRequestDto towerDto) {
        return ResponseEntity.ok(towerMapper.toDto(towerService.updateTower(id, towerDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTower(@PathVariable Long id) {
        towerService.deleteTower(id);
        return ResponseEntity.noContent().build();
    }
}