package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;

import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    // Métodos que devuelven Page
    Page<Payment> findByUserId(Long userId, Pageable pageable);
    Page<Payment> findByApartmentId(Long apartmentId, Pageable pageable);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    Page<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Métodos existentes que devuelven List (si aún los necesitas)
    List<Payment> findByUserId(Long userId);
    List<Payment> findByApartmentId(Long apartmentId);
    List<Payment> findByStatus(PaymentStatus status); // No paginado
    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate); // No paginado
}