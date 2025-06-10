package com.conjuntoresidencial.api.domain.payment.port.in;

import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import com.conjuntoresidencial.api.infrastructure.web.dto.request.PaymentRequestDto;
import java.util.List;
import java.time.LocalDate;
// Importa PaymentStatus si lo usas como parámetro de filtro

public interface ManagePaymentUseCase {
    Payment recordPayment(PaymentRequestDto paymentDto, String authenticatedUsername);
    Payment getPaymentById(Long id);
    List<Payment> getAllPayments();
    List<Payment> getPaymentsByUserId(Long userId);
    List<Payment> getPaymentsByApartmentId(Long apartmentId);
    // List<Payment> getPaymentsByDateRange(LocalDate start, LocalDate end);
    Payment updatePaymentStatus(Long paymentId, PaymentStatus newStatus, String authenticatedUsername);
    // O un método de actualización más general:
    // Payment updatePayment(Long paymentId, PaymentRequestDto paymentDto, String authenticatedUsername);
    void deletePayment(Long id);
}