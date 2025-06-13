package com.conjuntoresidencial.api.domain.vehicle.port.in;

import com.conjuntoresidencial.api.domain.vehicle.model.Vehicle;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VehicleRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VehicleResponseDto; // Usaremos un DTO enriquecido para la lista
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManageVehicleUseCase {
    Vehicle createVehicle(VehicleRequestDto vehicleDto);
    Vehicle getVehicleById(Long id);
    Vehicle getVehicleByPlate(String plate);
    // Para el listado complejo, devolvemos un DTO que ya puede tener la info de torre/apto
    Page<VehicleResponseDto> getAllVehiclesFiltered(String plate, Long towerId, Long apartmentId,
                                                    VehicleType type, VehicleStatus status,
                                                    String residentNameOrDocument, Pageable pageable);
    Vehicle updateVehicle(Long id, VehicleRequestDto vehicleDto);
    void deleteVehicle(Long id);
}