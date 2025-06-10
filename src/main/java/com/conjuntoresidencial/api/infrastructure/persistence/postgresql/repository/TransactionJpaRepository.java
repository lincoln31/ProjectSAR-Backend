package com.conjuntoresidencial.api.infrastructure.persistence.postgresql.repository;

import com.conjuntoresidencial.api.domain.transaction.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Para consultas más complejas si es necesario
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransactionJpaRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByApartmentId(Long apartmentId);
    List<Transaction> findByRelatedPaymentId(Long paymentId);
    // Ejemplo con JPQL para más flexibilidad
    // @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId ORDER BY t.transactionDate DESC")
    // List<Transaction> findAllByUserIdOrderByDateDesc(@Param("userId") Long userId);
}