package org.example.homework_1.repository.RepositiryInterfaces;

import org.example.homework_1.models.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryInterface {
    void addTransaction(Transaction transaction);

    List<Transaction> getUserTransactions(UUID userId);

    List<Transaction> getUserExpenseTransactions(UUID userId);

    boolean deleteTransaction(UUID uuid, UUID transactionId);

    boolean upgradeTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction);
}
