package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;

import com.conjuntoresidencial.api.domain.payment.model.Payment;
import com.conjuntoresidencial.api.domain.payment.model.PaymentStatus;
import com.conjuntoresidencial.api.domain.payment.port.out.PaymentRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.PaymentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements PaymentRepositoryPort {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentJpaRepository.findById(id);
    }

    // --- MÉTODOS PAGINADOS ---
    @Override
    public Page<Payment> findAll(Pageable pageable) { // Devuelve Page<Payment>
        return paymentJpaRepository.findAll(pageable);
    }

    @Override
    public Page<Payment> findByUserId(Long userId, Pageable pageable) { // Devuelve Page<Payment>
        return paymentJpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Payment> findByApartmentId(Long apartmentId, Pageable pageable) { // Devuelve Page<Payment>
        return paymentJpaRepository.findByApartmentId(apartmentId, pageable);
    }

    @Override
    public Page<Payment> findByStatus(PaymentStatus status, Pageable pageable) { // Devuelve Page<Payment>
        return paymentJpaRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) { // Devuelve Page<Payment>
        return paymentJpaRepository.findByPaymentDateBetween(startDate, endDate, pageable);
    }

    // --- MÉTODOS NO PAGINADOS (QUE DEVUELVEN LIST) ---
    // Estos deben coincidir con los que definiste en PaymentRepositoryPort que devuelven List
    @Override
    public List<Payment> findAll() { // Este es el findAll sin paginación
        return paymentJpaRepository.findAll();
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return paymentJpaRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> findByApartmentId(Long apartmentId) {
        return paymentJpaRepository.findByApartmentId(apartmentId);
    }

    @Override
    public List<Payment> findByStatus(PaymentStatus status) {
        return paymentJpaRepository.findByStatus(status);
    }

    @Override
    public List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate) {
        return paymentJpaRepository.findByPaymentDateBetween(startDate, endDate);
    }
    // --- FIN DE MÉTODOS NO PAGINADOS ---


    @Override
    public void deleteById(Long id) {
        paymentJpaRepository.deleteById(id);
    }
}