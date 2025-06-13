package com.conjuntoresidencial.api.domain.visitorlog.port.in;


import com.conjuntoresidencial.api.domain.visitorlog.model.VisitorLog;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VisitorLogRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VisitorLogResponseDto; // Para el listado
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface ManageVisitorLogUseCase {
    VisitorLog recordEntry(VisitorLogRequestDto entryDto, String registeredByUsername);
    VisitorLog recordExit(Long visitorLogId, LocalDateTime exitTimestamp, String registeredByUsername);
    VisitorLog getVisitorLogById(Long id);
    Page<VisitorLogResponseDto> getAllVisitorLogsFiltered(
            String visitorName, Long residentVisitedId, Long apartmentVisitedId,
            LocalDateTime entryDateFrom, LocalDateTime entryDateTo,
            Boolean currentlyInside, Pageable pageable);
}