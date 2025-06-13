package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.application.visitorlog.service.VisitorLogManagementService;
import com.conjuntoresidencial.api.domain.visitorlog.port.in.ManageVisitorLogUseCase;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VisitorLogExitRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.VisitorLogRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.VisitorLogResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.VisitorLogMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/visitor-logs")
@RequiredArgsConstructor
public class VisitorLogController {
    private final ManageVisitorLogUseCase visitorLogUseCase; // Inyectar UseCase
    private final VisitorLogMapper visitorLogMapper;

    @PostMapping("/entry")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIA')") // Asumiendo un rol GUARDIA o similar
    public ResponseEntity<VisitorLogResponseDto> recordEntry(
            @Valid @RequestBody VisitorLogRequestDto entryDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String registeredByUsername = userDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitorLogMapper.toDto(visitorLogUseCase.recordEntry(entryDto, registeredByUsername)));
    }

    @PatchMapping("/{id}/exit")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUARDIA')")
    public ResponseEntity<VisitorLogResponseDto> recordExit(
            @PathVariable Long id,
            @Valid @RequestBody VisitorLogExitRequestDto exitDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String registeredByUsername = userDetails.getUsername(); // Para auditoría si es necesario
        return ResponseEntity.ok(
                visitorLogMapper.toDto(visitorLogUseCase.recordExit(id, exitDto.getExitTimestamp(), registeredByUsername))
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Podría ser más restrictivo
    public ResponseEntity<VisitorLogResponseDto> getVisitorLogById(@PathVariable Long id) {
        return ResponseEntity.ok(visitorLogMapper.toDto(visitorLogUseCase.getVisitorLogById(id)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()") // Podría ser más restrictivo
    public ResponseEntity<Page<VisitorLogResponseDto>> getAllVisitorLogsFiltered(
            @RequestParam(required = false) String visitorName,
            @RequestParam(required = false) Long residentVisitedId,
            @RequestParam(required = false) Long apartmentVisitedId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime entryDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime entryDateTo,
            @RequestParam(required = false) Boolean currentlyInside, // true para los que están dentro, false para los que salieron
            @PageableDefault(size = 10, sort = "entryTimestamp") Pageable pageable) {

        Page<VisitorLogResponseDto> logsPage = visitorLogUseCase.getAllVisitorLogsFiltered(
                visitorName, residentVisitedId, apartmentVisitedId,
                entryDateFrom, entryDateTo, currentlyInside, pageable
        );
        return ResponseEntity.ok(logsPage);
    }
}