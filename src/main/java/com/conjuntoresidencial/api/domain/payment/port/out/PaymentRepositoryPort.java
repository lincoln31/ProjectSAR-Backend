package com.conjuntoresidencial.api.domain.payment.port.out;
import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    List<Payment> findAll();
    List<Payment> findByUserId(Long userId);
    List<Payment> findByApartmentId(Long apartmentId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);
    // Puedes añadir más métodos de búsqueda según necesidad
    void deleteById(Long id);
}