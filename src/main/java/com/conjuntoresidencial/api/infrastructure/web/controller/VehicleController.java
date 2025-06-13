package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.application.vehicle.service.VehicleManagementService;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import com.conjuntoresidencial.api.domain.vehicle.port.in.ManageVehicleUseCase;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VehicleRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VehicleResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.VehicleMapper; // Aunque el servicio devuelve DTO, el mapper puede ser útil para request
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {
    private final ManageVehicleUseCase vehicleUseCase; // Inyectar el UseCase
    private final VehicleMapper vehicleMapper; // Para mapear el Vehicle devuelto por create/update a DTO si es necesario

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // O un rol específico de gestión de vehículos
    public ResponseEntity<VehicleResponseDto> createVehicle(@Valid @RequestBody VehicleRequestDto vehicleDto) {
        // El servicio createVehicle devuelve la entidad Vehicle, el mapper la convierte a DTO
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleMapper.toDto(vehicleUseCase.createVehicle(vehicleDto)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Permitir a usuarios autenticados ver detalles si tienen permiso
    public ResponseEntity<VehicleResponseDto> getVehicleById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleMapper.toDto(vehicleUseCase.getVehicleById(id)));
    }

    @GetMapping("/plate/{plate}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VehicleResponseDto> getVehicleByPlate(@PathVariable String plate) {
        return ResponseEntity.ok(vehicleMapper.toDto(vehicleUseCase.getVehicleByPlate(plate)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // O rol específico para ver el listado
    public ResponseEntity<Page<VehicleResponseDto>> getAllVehiclesFiltered(
            @RequestParam(required = false) String plate,
            @RequestParam(required = false) Long towerId,
            @RequestParam(required = false) Long apartmentId,
            @RequestParam(required = false) VehicleType type,
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) String residentNameOrDocument,
            @PageableDefault(size = 10, sort = "plate") Pageable pageable) {

        Page<VehicleResponseDto> vehiclesPage = vehicleUseCase.getAllVehiclesFiltered(
                plate, towerId, apartmentId, type, status, residentNameOrDocument, pageable
        );
        return ResponseEntity.ok(vehiclesPage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VehicleResponseDto> updateVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequestDto vehicleDto) {
        return ResponseEntity.ok(vehicleMapper.toDto(vehicleUseCase.updateVehicle(id, vehicleDto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleUseCase.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}