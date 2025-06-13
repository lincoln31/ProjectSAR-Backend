package com.conjuntoresidencial.api.domain.payment.port.out;
import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);

    // Métodos para paginación
    Page<Payment> findAll(Pageable pageable); // Ya debería estar si JpaRepository es la base
    Page<Payment> findByUserId(Long userId, Pageable pageable); // <--- AÑADIR/MODIFICAR ESTE
    Page<Payment> findByApartmentId(Long apartmentId, Pageable pageable); // <--- AÑADIR/MODIFICAR ESTE
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable); // <--- AÑADIR/MODIFICAR ESTE (si lo necesitas paginado)
    Page<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable); // <--- AÑADIR/MODIFICAR ESTE (si lo necesitas paginado)

    // Mantener versiones que devuelven List si aún las necesitas para otros casos de uso internos
    // o si tu interfaz ManagePaymentUseCase aún tiene métodos que devuelven List y no Page.
    List<Payment> findAll(); // Este es el findAll sin paginación
    List<Payment> findByUserId(Long userId); // <--- MÉTODO EXISTENTE QUE DEVUELVE LIST
    List<Payment> findByApartmentId(Long apartmentId); // <--- MÉTODO EXISTENTE QUE DEVUELVE LIST
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    void deleteById(Long id);
}