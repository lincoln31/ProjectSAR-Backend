package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.application.transaction.service.TransactionManagementService;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.TransactionResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionManagementService transactionService; // O ManageTransactionUseCase
    private final TransactionMapper transactionMapper;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // O lógica más fina para usuarios
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionMapper.toDto(transactionService.getTransactionById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponseDto>> getAllTransactions(
            // Aquí podrías añadir @RequestParam para filtros como userId, apartmentId, dateRange, type
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long apartmentId
    ) {
        List<TransactionResponseDto> dtos;
        if (userId != null) {
            dtos = transactionService.getTransactionsByUserId(userId).stream()
                    .map(transactionMapper::toDto)
                    .collect(Collectors.toList());
        } else if (apartmentId != null) {
            dtos = transactionService.getTransactionsByApartmentId(apartmentId).stream()
                    .map(transactionMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            dtos = transactionService.getAllTransactions().stream()
                    .map(transactionMapper::toDto)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(dtos);
    }
}