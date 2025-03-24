package org.example.homework_1.repository.RepositiryInterfaces;

import org.example.homework_1.models.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionRepositoryInterface {
    void addTransaction(Transaction transaction);

    List<Transaction> getUserTransactions(Long userId);

    List<Transaction> getUserExpenseTransactions(Long userId);

    boolean deleteTransaction(Long uuid, Long transactionId);

    boolean upgradeTransaction(Long userId, Long transactionId, Transaction updatedTransaction);
}
