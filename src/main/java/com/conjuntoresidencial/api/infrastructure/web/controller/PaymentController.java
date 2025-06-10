package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.application.payment.service.PaymentManagementService; // Tu servicio
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.PaymentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.PaymentResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.PaymentMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentManagementService paymentService; // Inyectar ManagePaymentUseCase si prefieres
    private final PaymentMapper paymentMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo admins pueden registrar pagos
    public ResponseEntity<PaymentResponseDto> recordPayment(
            @Valid @RequestBody PaymentRequestDto paymentDto,
            @AuthenticationPrincipal UserDetails adminUserDetails) {
        String adminUsername = adminUserDetails.getUsername();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentMapper.toDto(paymentService.recordPayment(paymentDto, adminUsername)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESIDENTE')") // O una lógica más fina para que residente vea sus pagos
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long id) {
        // Aquí necesitarías lógica para verificar si el residente autenticado es el dueño del pago,
        // si no es admin. Por simplicidad, por ahora lo dejamos así.
        return ResponseEntity.ok(paymentMapper.toDto(paymentService.getPaymentById(id)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Solo admins ven todos los pagos
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<PaymentResponseDto> dtos = paymentService.getAllPayments().stream()
                .map(paymentMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // TODO: Endpoints para getPaymentsByUserId, getPaymentsByApartmentId

    @PatchMapping("/{paymentId}/status") // Usar PATCH para actualizaciones parciales como el estado
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status, // Recibir el nuevo estado como parámetro de request
            @AuthenticationPrincipal UserDetails adminUserDetails) {
        String adminUsername = adminUserDetails.getUsername();
        return ResponseEntity.ok(
                paymentMapper.toDto(paymentService.updatePaymentStatus(paymentId, status, adminUsername))
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}