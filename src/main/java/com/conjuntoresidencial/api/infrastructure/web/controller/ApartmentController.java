package com.conjuntoresidencial.api.infrastructure.web.controller;


import com.conjuntoresidencial.api.application.property.service.ApartmentManagementService;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ApartmentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.ApartmentResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.ApartmentMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/apartments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Proteger todas las operaciones de apartamentos
public class ApartmentController {
    private final ApartmentManagementService apartmentService;
    private final ApartmentMapper apartmentMapper;

    @PostMapping
    public ResponseEntity<ApartmentResponseDto> createApartment(@Valid @RequestBody ApartmentRequestDto apartmentDto) {

        System.out.println("Controller "+apartmentDto.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED)


                .body(apartmentMapper.toDto(apartmentService.createApartment(apartmentDto)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApartmentResponseDto> getApartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(apartmentMapper.toDto(apartmentService.getApartmentById(id)));
    }

    @GetMapping
    public ResponseEntity<List<ApartmentResponseDto>> getAllApartments(
            @RequestParam(required = false) Long towerId) { // Opcional para filtrar por torre
        List<ApartmentResponseDto> dtos;
        if (towerId != null) {
            dtos = apartmentService.getApartmentsByTowerId(towerId).stream()
                    .map(apartmentMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            dtos = apartmentService.getAllApartments().stream()
                    .map(apartmentMapper::toDto)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentResponseDto> updateApartment(@PathVariable Long id, @Valid @RequestBody ApartmentRequestDto apartmentDto) {
        return ResponseEntity.ok(apartmentMapper.toDto(apartmentService.updateApartment(id, apartmentDto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApartment(@PathVariable Long id) {
        apartmentService.deleteApartment(id);
        return ResponseEntity.noContent().build();
    }
}