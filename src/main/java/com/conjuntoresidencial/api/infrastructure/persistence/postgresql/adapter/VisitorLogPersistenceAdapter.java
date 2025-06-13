package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;

import com.conjuntoresidencial.api.domain.visitorlog.model.VisitorLog;
import com.conjuntoresidencial.api.domain.visitorlog.port.out.VisitorLogRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.VisitorLogJpaRepository;
import com.conjuntoresidencial.api.infrastructure.persistence.specification.VisitorLogSpecification; // Crearemos esto
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VisitorLogPersistenceAdapter implements VisitorLogRepositoryPort {
    private final VisitorLogJpaRepository visitorLogJpaRepository;

    @Override public VisitorLog save(VisitorLog visitorLog) { return visitorLogJpaRepository.save(visitorLog); }
    @Override public Optional<VisitorLog> findById(Long id) { return visitorLogJpaRepository.findById(id); }
    @Override public Page<VisitorLog> findAll(Pageable pageable) { return visitorLogJpaRepository.findAll(pageable); }

    @Override
    public Page<VisitorLog> findByCriteria(String visitorName, Long residentVisitedId, Long apartmentVisitedId,
                                           LocalDateTime entryDateFrom, LocalDateTime entryDateTo,
                                           Boolean currentlyInside, Pageable pageable) {
        Specification<VisitorLog> spec = VisitorLogSpecification.byCriteria(
                visitorName, residentVisitedId, apartmentVisitedId, entryDateFrom, entryDateTo, currentlyInside
        );
        return visitorLogJpaRepository.findAll(spec, pageable);
    }

    @Override
    public List<VisitorLog> findActiveVisitsByApartment(Long apartmentId) {
        return visitorLogJpaRepository.findActiveVisitsByApartmentId(apartmentId);
    }
}