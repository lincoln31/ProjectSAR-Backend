package com.conjuntoresidencial.api.domain.transaction.port.out;

import com.conjuntoresidencial.api.domain.transaction.model.Transaction;
import java.util.List;
import java.util.Optional;
// Importar otros filtros si son necesarios, ej. LocalDate, TransactionType

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(Long id);
    List<Transaction> findAll(); // Podría necesitar paginación para muchas transacciones
    List<Transaction> findByUserId(Long userId);
    List<Transaction> findByApartmentId(Long apartmentId);
    List<Transaction> findByRelatedPaymentId(Long paymentId);
    // Métodos de búsqueda adicionales (por tipo, por rango de fechas, etc.)
}