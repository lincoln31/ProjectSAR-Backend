package com.conjuntoresidencial.api.application.transaction.service;
import com.conjuntoresidencial.api.domain.transaction.model.Transaction;
import com.conjuntoresidencial.api.domain.transaction.port.in.ManageTransactionUseCase;
import com.conjuntoresidencial.api.domain.transaction.port.out.TransactionRepositoryPort;
import com.conjuntoresidencial.api.domain.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionManagementService implements ManageTransactionUseCase {

    private final TransactionRepositoryPort transactionRepository;

    // Método para inicializar entidades LAZY necesarias para el DTO
    private void initializeLazyFields(Transaction transaction) {
        if (transaction == null) return;
        if (transaction.getRelatedPayment() != null) transaction.getRelatedPayment().getId(); // Cargar ID es suficiente
        if (transaction.getUser() != null) transaction.getUser().getUsername(); // O get UserProfile si el mapper lo usa
        if (transaction.getApartment() != null) {
            transaction.getApartment().getNumber();
            if (transaction.getApartment().getTower() != null) transaction.getApartment().getTower().getName();
        }
        if (transaction.getCreatedBy() != null) transaction.getCreatedBy().getUsername();
    }


    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
        initializeLazyFields(transaction);
        return transaction;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll(); // Implementar paginación aquí si es necesario
        transactions.forEach(this::initializeLazyFields);
        return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        transactions.forEach(this::initializeLazyFields);
        return transactions;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByApartmentId(Long apartmentId) {
        List<Transaction> transactions = transactionRepository.findByApartmentId(apartmentId);
        transactions.forEach(this::initializeLazyFields);
        return transactions;
    }
}