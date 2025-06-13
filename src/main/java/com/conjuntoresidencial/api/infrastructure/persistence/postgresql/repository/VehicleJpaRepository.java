package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;

import com.conjuntoresidencial.api.domain.vehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // Para búsquedas dinámicas
import java.util.Optional;

// JpaSpecificationExecutor es para usar el Criteria API para queries dinámicas
public interface VehicleJpaRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    Optional<Vehicle> findByLicensePlate(String licensePlate); // NUEVO
    boolean existsByLicensePlate(String licensePlate);
}