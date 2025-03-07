package org.example.homework_1.services;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.TransactionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;



public class TransactionService {
    private final TransactionRepository transactionRepository;
    LocalDate now = LocalDate.now();

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void addTransaction(UUID userId, TransactionType type, double amount, String category, String description) {
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(id,userId,type, amount, category, now, description);
        transactionRepository.addTransaction(transaction);
        System.out.println("Транзакция добавлена.");

    }
    public List<Transaction> showUserTransactions(UUID userId) {
        List<Transaction> transactions = transactionRepository.getUserTransactions(userId);
        if (transactions.isEmpty()) {
            System.out.println(" У вас нет записей.");
        }
        int index = 1;
        for (Transaction t : transactions) {
            System.out.println(index +". "+ t.getType() + ": " + t.getAmount() + " |" + t.getCategory() + " | " + t.getDescription());
            index++;
        }
        return transactions;
    }
    public boolean updateTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction){
        return transactionRepository.upgradeTransaction(userId, transactionId, updatedTransaction);
        }


    public void deleteTransaction(UUID userId, UUID transactionId) {
        System.out.println("Удаление транзакции " + transactionId);
        transactionRepository.deleteTransaction(userId, transactionId);
    }
}
