package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;

import com.conjuntoresidencial.api.domain.vehicle.model.Vehicle;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleStatus;
import com.conjuntoresidencial.api.domain.vehicle.model.VehicleType;
import com.conjuntoresidencial.api.domain.vehicle.port.out.VehicleRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.VehicleJpaRepository;
import com.conjuntoresidencial.api.infrastructure.persistence.specification.VehicleSpecification; // Crearemos esto
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VehiclePersistenceAdapter implements VehicleRepositoryPort {
    private final VehicleJpaRepository vehicleJpaRepository;

    @Override public Vehicle save(Vehicle vehicle) { return vehicleJpaRepository.save(vehicle); }
    @Override public Optional<Vehicle> findById(Long id) { return vehicleJpaRepository.findById(id); }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return Optional.empty();
    }

    @Override public List<Vehicle> findAll() { return vehicleJpaRepository.findAll(); }
    @Override public void deleteById(Long id) { vehicleJpaRepository.deleteById(id); }

    @Override
    public boolean existsByPlate(String plate) {
        return false;
    }


    @Override
    public Optional<Vehicle> findByLicensePlate(String licensePlate) {
        return vehicleJpaRepository.findByLicensePlate(licensePlate); // ¡CORREGIDO!
    }

    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return vehicleJpaRepository.existsByLicensePlate(licensePlate); // ¡CORREGIDO!
    }


    @Override
    public Page<Vehicle> findByCriteria(String plate, Long towerId, Long apartmentId, VehicleType type,
                                        VehicleStatus status, String residentNameOrDocument, Pageable pageable) {
        Specification<Vehicle> spec = VehicleSpecification.byCriteria(
                plate, towerId, apartmentId, type, status, residentNameOrDocument
        );
        return vehicleJpaRepository.findAll(spec, pageable);
    }
}