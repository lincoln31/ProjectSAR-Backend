package com.conjuntoresidencial.api.application.visitorlog.service;

import com.conjuntoresidencial.api.domain.property.model.Apartment;
import com.conjuntoresidencial.api.domain.property.port.out.ApartmentRepositoryPort;
import com.conjuntoresidencial.api.domain.user.model.User;
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort;
import com.conjuntoresidencial.api.domain.visitorlog.model.VisitorLog;
import com.conjuntoresidencial.api.domain.visitorlog.port.in.ManageVisitorLogUseCase;
import com.conjuntoresidencial.api.domain.visitorlog.port.out.VisitorLogRepositoryPort;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VisitorLogRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VisitorLogResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.VisitorLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VisitorLogManagementService implements ManageVisitorLogUseCase {
    private final VisitorLogRepositoryPort visitorLogRepository;
    private final UserRepositoryPort userRepository;
    private final ApartmentRepositoryPort apartmentRepository;
    private final VisitorLogMapper visitorLogMapper; // Para el DTO de respuesta del listado

    private void initializeLazyFieldsForDto(VisitorLog log) {
        if (log == null) return;
        if (log.getResidentVisited() != null) {
            log.getResidentVisited().getUsername(); // O cargar perfil si es necesario para el mapper
            if(log.getResidentVisited().getUserProfile() != null) log.getResidentVisited().getUserProfile().getFirstName();
        }
        if (log.getApartmentVisited() != null) {
            log.getApartmentVisited().getNumber();
            if (log.getApartmentVisited().getTower() != null) log.getApartmentVisited().getTower().getName();
        }
        if (log.getRegisteredBy() != null) {
            log.getRegisteredBy().getUsername();
            if(log.getRegisteredBy().getUserProfile() != null) log.getRegisteredBy().getUserProfile().getFirstName();
        }
    }

    private VisitorLogResponseDto enrichResponseDto(VisitorLog log) {
        // Asegurar que los campos LAZY estén cargados antes de mapear
        initializeLazyFieldsForDto(log);
        return visitorLogMapper.toDto(log);
    }


    @Override
    @Transactional
    public VisitorLog recordEntry(VisitorLogRequestDto entryDto, String registeredByUsername) {
        User registeredBy = userRepository.findByUsername(registeredByUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User (registrar) not found: " + registeredByUsername));
        User residentVisited = userRepository.findById(entryDto.getResidentVisitedId())
                .orElseThrow(() -> new ResourceNotFoundException("Resident to be visited not found: " + entryDto.getResidentVisitedId()));
        Apartment apartmentVisited = apartmentRepository.findById(entryDto.getApartmentVisitedId())
                .orElseThrow(() -> new ResourceNotFoundException("Apartment to be visited not found: " + entryDto.getApartmentVisitedId()));

        // Forzar carga para el mapper si se devuelve la entidad y el mapper se usa en el controller
        initializeLazyFieldsForDto(VisitorLog.builder().residentVisited(residentVisited).apartmentVisited(apartmentVisited).registeredBy(registeredBy).build());


        VisitorLog newLog = VisitorLog.builder()
                .visitorFullName(entryDto.getVisitorFullName())
                .visitorPhoneNumber(entryDto.getVisitorPhoneNumber())
                .residentVisited(residentVisited)
                .apartmentVisited(apartmentVisited)
                .entryTimestamp(entryDto.getEntryTimestamp())
                .observations(entryDto.getObservations())
                .registeredBy(registeredBy)
                .build();
        return visitorLogRepository.save(newLog);
    }

    @Override
    @Transactional
    public VisitorLog recordExit(Long visitorLogId, LocalDateTime exitTimestamp, String registeredByUsername) {
        // Podrías verificar si registeredByUsername tiene permiso para registrar la salida
        VisitorLog log = getVisitorLogById(visitorLogId); // Reusa el método que ya inicializa campos
        if (log.getExitTimestamp() != null) {
            throw new IllegalStateException("Visitor already exited at " + log.getExitTimestamp());
        }
        log.setExitTimestamp(exitTimestamp);
        return visitorLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public VisitorLog getVisitorLogById(Long id) {
        VisitorLog log = visitorLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor log not found: " + id));
        initializeLazyFieldsForDto(log); // Asegurar carga para el DTO
        return log;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitorLogResponseDto> getAllVisitorLogsFiltered(
            String visitorName, Long residentVisitedId, Long apartmentVisitedId,
            LocalDateTime entryDateFrom, LocalDateTime entryDateTo,
            Boolean currentlyInside, Pageable pageable) {

        Page<VisitorLog> logsPage = visitorLogRepository.findByCriteria(
                visitorName, residentVisitedId, apartmentVisitedId,
                entryDateFrom, entryDateTo, currentlyInside, pageable
        );
        return logsPage.map(this::enrichResponseDto); // Mapear y enriquecer
    }
}