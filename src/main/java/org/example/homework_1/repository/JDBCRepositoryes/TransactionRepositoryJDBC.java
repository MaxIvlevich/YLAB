package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;

import java.util.List;
import java.util.UUID;

public class TransactionRepositoryJDBC implements TransactionRepositoryInterface {
    @Override
    public void addTransaction(Transaction transaction) {

    }

    @Override
    public List<Transaction> getUserTransactions(UUID userId) {
        return null;
    }

    @Override
    public List<Transaction> getUserExpenseTransactions(UUID userId) {
        return null;
    }

    @Override
    public boolean deleteTransaction(UUID uuid, UUID transactionId) {
        return false;
    }

    @Override
    public boolean upgradeTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction) {
        return false;
    }
}
