package com.conjuntoresidencial.api.domain.property.port.in;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.model.Tower;
// Usaremos DTOs para las operaciones, adaptados a las necesidades de la API
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ApartmentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.TowerRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.ApartmentResponseDto; // O devolver la entidad
import com.conjuntoresidencial.api.infrastructure.web.dto.response.TowerResponseDto; // O devolver la entidad

import java.util.List;

public interface PropertyManagementUseCase {
    // Operaciones de Torre
    TowerResponseDto createTower(TowerRequestDto towerRequestDto);
    TowerResponseDto getTowerById(Long towerId);
    List<TowerResponseDto> getAllTowers();
    TowerResponseDto updateTower(Long towerId, TowerRequestDto towerRequestDto);
    void deleteTower(Long towerId);

    // Operaciones de Apartamento
    ApartmentResponseDto createApartment(Long towerId, ApartmentRequestDto apartmentRequestDto);
    ApartmentResponseDto getApartmentById(Long apartmentId);
    List<ApartmentResponseDto> getApartmentsByTowerId(Long towerId);
    ApartmentResponseDto updateApartment(Long apartmentId, ApartmentRequestDto apartmentRequestDto);
    void deleteApartment(Long apartmentId);
}