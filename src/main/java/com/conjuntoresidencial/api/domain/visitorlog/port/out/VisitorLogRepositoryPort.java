package com.conjuntoresidencial.api.domain.visitorlog.port.out;

import com.conjuntoresidencial.api.domain.visitorlog.model.VisitorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitorLogRepositoryPort {
    VisitorLog save(VisitorLog visitorLog);
    Optional<VisitorLog> findById(Long id);
    Page<VisitorLog> findAll(Pageable pageable); // Siempre es bueno paginar listas grandes
    // Métodos de búsqueda/filtrado
    Page<VisitorLog> findByCriteria(String visitorName, Long residentVisitedId, Long apartmentVisitedId,
                                    LocalDateTime entryDateFrom, LocalDateTime entryDateTo,
                                    Boolean currentlyInside, Pageable pageable);
    List<VisitorLog> findActiveVisitsByApartment(Long apartmentId); // Visitantes actualmente dentro de un apto
}