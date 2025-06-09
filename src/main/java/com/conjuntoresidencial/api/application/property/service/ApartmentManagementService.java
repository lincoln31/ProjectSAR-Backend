package com.conjuntoresidencial.api.application.property.service;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.model.Tower;
import com.conjuntoresidencial.api.domain.property.port.in.ManageApartmentUseCase; // Crear esta interfaz
import com.conjuntoresidencial.api.domain.property.port.out.ApartmentRepositoryPort;
import com.conjuntoresidencial.api.domain.property.port.out.TowerRepositoryPort;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ApartmentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.ApartmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentManagementService implements ManageApartmentUseCase { // Implementar la interfaz
    private final ApartmentRepositoryPort apartmentRepository;
    private final TowerRepositoryPort towerRepository;
    private final ApartmentMapper apartmentMapper;

    @Override
    @Transactional
    public Apartment createApartment(ApartmentRequestDto apartmentDto) {
        Tower tower = towerRepository.findById(apartmentDto.getTowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + apartmentDto.getTowerId()));

        if(apartmentRepository.findByNumberAndTower(apartmentDto.getNumber(), tower).isPresent()){
            throw new IllegalArgumentException("Apartment " + apartmentDto.getNumber() + " already exists in tower " + tower.getName());
        }

        Apartment apartment = apartmentMapper.toDomain(apartmentDto);

        System.out.println("property service  "+apartment.getStatus());

        apartment.setTower(tower); // Asignar la torre recuperada
        return apartmentRepository.save(apartment);
    }

    @Override
    @Transactional(readOnly = true)
    public Apartment getApartmentById(Long id) {
        return apartmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Apartment> getAllApartments() {
        return apartmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Apartment> getApartmentsByTowerId(Long towerId) {
        Tower tower = towerRepository.findById(towerId)
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + towerId));
        return apartmentRepository.findByTower(tower);
    }


    @Override
    @Transactional
    public Apartment updateApartment(Long id, ApartmentRequestDto apartmentDto) {
        Apartment existingApartment = getApartmentById(id);
        Tower tower = towerRepository.findById(apartmentDto.getTowerId())
                .orElseThrow(() -> new ResourceNotFoundException("Tower not found with id: " + apartmentDto.getTowerId()));

        // Verificar unicidad si el número o la torre cambian
        if (!existingApartment.getNumber().equals(apartmentDto.getNumber()) || !existingApartment.getTower().getId().equals(tower.getId())) {
            if(apartmentRepository.findByNumberAndTower(apartmentDto.getNumber(), tower).isPresent()){
                throw new IllegalArgumentException("Apartment " + apartmentDto.getNumber() + " already exists in tower " + tower.getName());
            }
        }

        apartmentMapper.updateDomainFromDto(apartmentDto, existingApartment);
        existingApartment.setTower(tower); // Re-asignar la torre
        return apartmentRepository.save(existingApartment);
    }

    @Override
    @Transactional
    public void deleteApartment(Long id) {
        if (!apartmentRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Apartment not found with id: " + id);
        }
        apartmentRepository.deleteById(id);
    }
}