package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;

import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import com.conjuntoresidencial.api.domain.payment.port.out.PaymentRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {
    private final PaymentJpaRepository paymentJpaRepository;

    @Override public Payment save(Payment payment) { return paymentJpaRepository.save(payment); }
    @Override public Optional<Payment> findById(Long id) { return paymentJpaRepository.findById(id); }
    @Override public List<Payment> findAll() { return paymentJpaRepository.findAll(); }
    @Override public List<Payment> findByUserId(Long userId) { return paymentJpaRepository.findByUserId(userId); }
    @Override public List<Payment> findByApartmentId(Long apartmentId) { return paymentJpaRepository.findByApartmentId(apartmentId); }
    @Override public List<Payment> findByStatus(PaymentStatus status) { return paymentJpaRepository.findByStatus(status); }
    @Override public List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate) { return paymentJpaRepository.findByPaymentDateBetween(startDate, endDate); }
    @Override public void deleteById(Long id) { paymentJpaRepository.deleteById(id); }
}