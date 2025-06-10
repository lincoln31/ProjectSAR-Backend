package com.conjuntoresidencial.api.domain.transaction.port.in;

import com.conjuntoresidencial.api.domain.transaction.model.Transaction;
import java.util.List;

public interface ManageTransactionUseCase {
    Transaction getTransactionById(Long id);
    List<Transaction> getAllTransactions(); // Considerar filtros y paginación
    List<Transaction> getTransactionsByUserId(Long userId);
    List<Transaction> getTransactionsByApartmentId(Long apartmentId);
    // Otros métodos de consulta según necesidad
}