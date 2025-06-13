package com.conjuntoresidencial.api.domain.vehicle.port.out;

import com.conjuntoresidencial.api.domain.vehicle.model.Vehicle;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import org.springframework.data.domain.Page; // Para paginación
import org.springframework.data.domain.Pageable; // Para paginación
import java.util.List;
import java.util.Optional;

public interface VehicleRepositoryPort {
    Vehicle save(Vehicle vehicle);
    Optional<Vehicle> findById(Long id);
    Optional<Vehicle> findByPlate(String plate);
    List<Vehicle> findAll(); // Para casos simples, pero el listado filtrado será más complejo
    void deleteById(Long id);
    boolean existsByPlate(String plate);
    Optional<Vehicle> findByLicensePlate(String licensePlate); // NUEVO
    boolean existsByLicensePlate(String licensePlate); // Ya deberías tener este


    // Método para búsqueda avanzada/filtrada (puede evolucionar)
    Page<Vehicle> findByCriteria(String plate, Long towerId, Long apartmentId, VehicleType type,
                                 VehicleStatus status, String residentNameOrDocument, Pageable pageable);
}