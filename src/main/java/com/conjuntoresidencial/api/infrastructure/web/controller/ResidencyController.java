package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.application.residency.service.ResidencyManagementService;
import com.conjuntoresidencial.api.infrastructure.security.jwt.JwtTokenProvider;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.ResidencyRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.ResidencyResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.ResidencyMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/residencies")
@RequiredArgsConstructor
public class ResidencyController {
    private final ResidencyManagementService residencyService;
    private final ResidencyMapper residencyMapper;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResidencyResponseDto> createResidency(@Valid @RequestBody ResidencyRequestDto residencyDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // --- REVIERTE A ESTA LÓGICA ---
        com.conjuntoresidencial.api.domain.residency.model.Residency createdResidency = residencyService.createResidency(residencyDto);
        ResidencyResponseDto responseDto = residencyMapper.toDto(createdResidency);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        // --- FIN DE LA LÓGICA CORRECTA ---
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.hasAccessToUserResidencies(#userId, authentication.principal)") // Seguridad más granular
    public ResponseEntity<List<ResidencyResponseDto>> getResidenciesByUserId(@PathVariable Long userId) {
        List<ResidencyResponseDto> dtos = residencyService.getResidenciesByUserId(userId).stream()
                .map(residencyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isUserAssociatedWithApartment(#apartmentId, authentication.principal)") // Seguridad más granular
    public ResponseEntity<List<ResidencyResponseDto>> getResidenciesByApartmentId(@PathVariable Long apartmentId) {
        List<ResidencyResponseDto> dtos = residencyService.getResidenciesByApartmentId(apartmentId).stream()
                .map(residencyMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // O lógica más granular si un usuario puede ver su propia vinculación por ID
    public ResponseEntity<ResidencyResponseDto> getResidencyById(@PathVariable Long id){
        return ResponseEntity.ok(residencyMapper.toDto(residencyService.getResidencyById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResidency(@PathVariable Long id) {
        residencyService.deleteResidency(id);
        return ResponseEntity.noContent().build();
    }
}