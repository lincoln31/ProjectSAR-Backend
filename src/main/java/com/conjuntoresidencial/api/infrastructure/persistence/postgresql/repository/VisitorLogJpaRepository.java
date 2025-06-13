package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;

import com.conjuntoresidencial.api.domain.visitorlog.model.VisitorLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitorLogJpaRepository extends JpaRepository<VisitorLog, Long>, JpaSpecificationExecutor<VisitorLog> {
    // Para encontrar visitas activas (sin hora de salida) para un apartamento específico
    @Query("SELECT vl FROM VisitorLog vl WHERE vl.apartmentVisited.id = :apartmentId AND vl.exitTimestamp IS NULL ORDER BY vl.entryTimestamp DESC")
    List<VisitorLog> findActiveVisitsByApartmentId(@Param("apartmentId") Long apartmentId);
}