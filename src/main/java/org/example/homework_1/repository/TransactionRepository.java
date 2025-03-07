package org.example.homework_1.repository;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;

import java.util.*;

public class TransactionRepository {
    private final Map<UUID,List<Transaction>> transactions = new HashMap<>();
    public void addTransaction(Transaction transaction) {
        transactions.putIfAbsent(transaction.getUserUUID(), new ArrayList<>());
        transactions.get(transaction.getUserUUID()).add(transaction);

    }

    public List<Transaction> getUserTransactions(UUID userId) {

        return transactions.getOrDefault(userId, new ArrayList<>());

    }
    public List<Transaction> getUserExpenseTransactions(UUID userId) {
        List<Transaction> expenseTransactions = new ArrayList<>();
        List<Transaction> userTransactions = transactions.get(userId);
        if (userTransactions != null) {
            for (Transaction transaction : userTransactions) {
                if (transaction.getType() == TransactionType.EXPENSE) {
                    expenseTransactions.add(transaction);
                }
            }
        }
        return expenseTransactions;
    }

    public boolean deleteTransaction(UUID  uuid, UUID transactionId) {
        List<Transaction> userTransactions = transactions.get(uuid);
        if (userTransactions != null) {
            return userTransactions.removeIf(t -> t.getTransactionUUID().equals(transactionId));
        }
        return false;
    }

    public boolean upgradeTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction){
        List<Transaction> userTransactions = transactions.get(userId);
        if (userTransactions != null) {
            for (int i = 0; i < userTransactions.size(); i++) {
                Transaction existingTransaction = userTransactions.get(i);
                if (existingTransaction.getTransactionUUID().equals(transactionId)) {
                    userTransactions.set(i, updatedTransaction);
                    System.out.println("Транзакция обновлена для пользователя: " + userId);
                    return true;
                }
            }
        }
        return false;

    }
}
