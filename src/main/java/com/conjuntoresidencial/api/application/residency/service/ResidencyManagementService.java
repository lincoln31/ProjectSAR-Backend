package com.conjuntoresidencial.api.application.residency.service;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.port.out.ApartmentRepositoryPort;
import com.conjuntoresidencial.api.domain.residency.model.Residency;
import com.conjuntoresidencial.api.domain.residency.port.in.ManageResidencyUseCase;
import com.conjuntoresidencial.api.domain.residency.port.out.ResidencyRepositoryPort;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ResidencyRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidencyManagementService implements ManageResidencyUseCase {
    private final ResidencyRepositoryPort residencyRepository;
    private final UserRepositoryPort userRepository;
    private final ApartmentRepositoryPort apartmentRepository;

    @Override
    @Transactional
    public Residency createResidency(ResidencyRequestDto residencyDto) {
        System.out.println("ResidencyService: inicio createResidency"); // Log más descriptivo

        User user = userRepository.findById(residencyDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + residencyDto.getUserId()));
        System.out.println("ResidencyService: User encontrado con ID: " + user.getId());

        Apartment apartment = apartmentRepository.findById(residencyDto.getApartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + residencyDto.getApartmentId()));
        System.out.println("ResidencyService: Apartment encontrado con ID: " + apartment.getId() + " y número: " + apartment.getNumber());

        if (residencyRepository.existsByUserAndApartmentAndResidencyType(user, apartment, residencyDto.getResidencyType())) {
            System.out.println("ResidencyService: Vinculación duplicada detectada.");
            throw new IllegalArgumentException("User " + user.getUsername() +
                    " is already assigned to apartment " + apartment.getNumber() +
                    " with type " + residencyDto.getResidencyType());
        }
        System.out.println("ResidencyService: No existe vinculación duplicada, procediendo a crear.");

        Residency residency = Residency.builder()
                .user(user)
                .apartment(apartment)
                .residencyType(residencyDto.getResidencyType())
                .build();

        Residency savedResidency = residencyRepository.save(residency);
        System.out.println("ResidencyService: Residency guardada con ID: " + savedResidency.getId());

        // --- FORZAR INICIALIZACIÓN DE CAMPOS REQUERIDOS POR EL ResidencyMapper ---
        // Esto se hace ANTES de que el método @Transactional termine y la sesión se cierre.

        // Para User y UserProfile (para userFullName)
        if (savedResidency.getUser() != null) {
            savedResidency.getUser().getUsername(); // Ya se accedió, pero por si acaso
            if (savedResidency.getUser().getUserProfile() != null) {
                // Acceder a los campos que el mapper usa explícitamente
                savedResidency.getUser().getUserProfile().getFirstName();
                savedResidency.getUser().getUserProfile().getLastName();
                System.out.println("ResidencyService: UserProfile inicializado para DTO.");
            } else {
                System.out.println("ResidencyService: UserProfile es null para el usuario: " + savedResidency.getUser().getUsername());
            }
        }

        // Para Apartment y Tower (para towerName y apartmentNumber)
        if (savedResidency.getApartment() != null) {
            savedResidency.getApartment().getNumber(); // Ya se accedió, pero por si acaso
            if (savedResidency.getApartment().getTower() != null) {
                savedResidency.getApartment().getTower().getName(); // CRUCIAL para inicializar Tower
                System.out.println("ResidencyService: Tower inicializada para DTO (Nombre: " + savedResidency.getApartment().getTower().getName() + ").");
            } else {
                System.out.println("ResidencyService: Tower es null para el apartamento: " + savedResidency.getApartment().getNumber());
            }
        }
        // --- FIN DE FORZAR INICIALIZACIÓN ---

        return savedResidency;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Residency> getResidenciesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        List<Residency> residencies = residencyRepository.findByUser(user);
        residencies.forEach(this::initializeResidencyForDto); // Inicializa cada una
        return residencies;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Residency> getResidenciesByApartmentId(Long apartmentId) {
        Apartment apartment = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Apartment not found with id: " + apartmentId));
        List<Residency> residencies = residencyRepository.findByApartment(apartment);
        residencies.forEach(this::initializeResidencyForDto); // Inicializa cada una
        return residencies;
    }


    // En ResidencyManagementService.java
    @Override
    @Transactional(readOnly = true) // readOnly = true es bueno para operaciones de solo lectura
    public Residency getResidencyById(Long id) {
        Residency residency = residencyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Residency not found with id: " + id));

        // Forzar inicialización de los campos necesarios para el DTO
        initializeResidencyForDto(residency); // Llama a un método ayudante
        return residency;
    }

    @Override
    @Transactional
    public void deleteResidency(Long id) {
        if (!residencyRepository.findById(id).isPresent()){
            throw new ResourceNotFoundException("Residency not found with id: " + id);
        }
        residencyRepository.deleteById(id);
    }
    private void initializeResidencyForDto(Residency residency) {
        if (residency == null) return;

        if (residency.getUser() != null) {
            residency.getUser().getUsername(); // Para username
            if (residency.getUser().getUserProfile() != null) {
                residency.getUser().getUserProfile().getFirstName(); // Para userFullName
                residency.getUser().getUserProfile().getLastName();
            }
        }
        if (residency.getApartment() != null) {
            residency.getApartment().getNumber(); // Para apartmentNumber
            if (residency.getApartment().getTower() != null) {
                residency.getApartment().getTower().getName(); // Para towerName
            }
        }
}}