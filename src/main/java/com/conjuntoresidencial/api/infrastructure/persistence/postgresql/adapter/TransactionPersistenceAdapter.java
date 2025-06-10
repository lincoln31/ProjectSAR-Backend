package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.adapter;

import com.conjuntoresidencial.api.domain.transaction.model.Transaction;
import com.conjuntoresidencial.api.domain.transaction.port.out.TransactionRepositoryPort;
import com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository.TransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TransactionPersistenceAdapter implements TransactionRepositoryPort {
    private final TransactionJpaRepository transactionJpaRepository;

    @Override public Transaction save(Transaction transaction) { return transactionJpaRepository.save(transaction); }
    @Override public Optional<Transaction> findById(Long id) { return transactionJpaRepository.findById(id); }
    @Override public List<Transaction> findAll() { return transactionJpaRepository.findAll(); } // Considerar paginación
    @Override public List<Transaction> findByUserId(Long userId) { return transactionJpaRepository.findByUserId(userId); }
    @Override public List<Transaction> findByApartmentId(Long apartmentId) { return transactionJpaRepository.findByApartmentId(apartmentId); }
    @Override public List<Transaction> findByRelatedPaymentId(Long paymentId) { return transactionJpaRepository.findByRelatedPaymentId(paymentId); }
}