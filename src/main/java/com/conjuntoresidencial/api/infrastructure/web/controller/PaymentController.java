package com.conjuntoresidencial.api.infrastructure.web.controller;

import com.conjuntoresidencial.api.application.payment.service.PaymentManagementService;
import com.conjuntoresidencial.api.domain.payment.model.Payment; // Entidad del dominio
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException; // Para el getMyPayments
import com.conjuntoresidencial.api.domain.user.model.User; // Para el getMyPayments
import com.conjuntoresidencial.api.domain.user.port.out.UserRepositoryPort; // Para el getMyPayments
import com.conjuntoresidencial.api.infrastructure.web.dto.request.PaymentRequestDto;
import com.conjuntoresidencial.api.infrastructure.web.dto.response.PaymentResponseDto;
import com.conjuntoresidencial.api.infrastructure.web.mapper.PaymentMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Correcta importación para Page
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; // Para valores por defecto de paginación
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
// No necesitamos List aquí si devolvemos Page o un solo DTO
// import java.util.List;
// import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentManagementService paymentService;
    private final PaymentMapper paymentMapper;
    private final UserRepositoryPort userRepository; // Necesario para obtener el User en getMyPayments

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> recordPayment(
            @Valid @RequestBody PaymentRequestDto paymentDto,
            @AuthenticationPrincipal UserDetails adminUserDetails) {
        String adminUsername = adminUserDetails.getUsername();
        Payment savedPayment = paymentService.recordPayment(paymentDto, adminUsername);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentMapper.toDto(savedPayment));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // La lógica de permisos está en el servicio
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) { // Inyectar UserDetails
        Payment payment = paymentService.getPaymentById(id, userDetails); // Pasar UserDetails al servicio
        return ResponseEntity.ok(paymentMapper.toDto(payment));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PaymentResponseDto>> getMyPayments(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "paymentDate") Pageable pageable) { // Añadir PageableDefault

        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found: " + userDetails.getUsername()));

        Page<Payment> paymentsPage = paymentService.getPaymentsByUserIdPaginated(currentUser.getId(), pageable, userDetails);
        Page<PaymentResponseDto> dtosPage = paymentsPage.map(paymentMapper::toDto);
        return ResponseEntity.ok(dtosPage);
    }

    @GetMapping
    // @PreAuthorize("hasRole('ADMIN')") // La lógica de permisos está en el servicio
    @PreAuthorize("isAuthenticated()") // Permitir a usuarios autenticados, el servicio filtrará
    public ResponseEntity<Page<PaymentResponseDto>> getAllPayments(
            @AuthenticationPrincipal UserDetails userDetails, // Necesario para la lógica de permisos en el servicio
            @PageableDefault(size = 10, sort = "paymentDate") Pageable pageable) { // Añadir Pageable y UserDetails

        Page<Payment> paymentsPage = paymentService.getAllPayments(pageable, userDetails); // Pasar Pageable y UserDetails
        Page<PaymentResponseDto> dtosPage = paymentsPage.map(paymentMapper::toDto);
        return ResponseEntity.ok(dtosPage);
    }

    // TODO: Endpoints para getPaymentsByUserId (si es diferente de /me) y getPaymentsByApartmentId
    // Ejemplo para getPaymentsByApartmentId (Solo Admin)
    @GetMapping("/apartment/{apartmentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentResponseDto>> getPaymentsByApartment(
            @PathVariable Long apartmentId,
            @AuthenticationPrincipal UserDetails userDetails, // Para pasar al servicio
            @PageableDefault(size = 10, sort = "paymentDate") Pageable pageable) {
        Page<Payment> paymentsPage = paymentService.getPaymentsByApartmentIdPaginated(apartmentId, pageable, userDetails);
        Page<PaymentResponseDto> dtosPage = paymentsPage.map(paymentMapper::toDto);
        return ResponseEntity.ok(dtosPage);
    }


    @PatchMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDto> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam PaymentStatus status,
            @AuthenticationPrincipal UserDetails adminUserDetails) {
        String adminUsername = adminUserDetails.getUsername(); // El servicio espera el username para registeredBy
        Payment updatedPayment = paymentService.updatePaymentStatus(paymentId, status, adminUsername);
        return ResponseEntity.ok(paymentMapper.toDto(updatedPayment));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}