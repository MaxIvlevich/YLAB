package org.example.homework_1.services.Interfaces;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TransactionServiceInterface {
    void addTransaction(UUID userId, TransactionType type, BigDecimal amount, String category, String description);

    List<Transaction> showUserTransactions(UUID userId);

    boolean updateTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction);

    void deleteTransaction(UUID userId, UUID transactionId);

    double getTotalIncome(UUID userId, LocalDate fromDate);

    double getTotalExpenses(UUID userId, LocalDate fromDate);

    void getTotalExpensesOrIncomeForPeriod(UUID userId, LocalDate fromDate);

    Map<String, Double> getExpensesByCategory(UUID userId, LocalDate fromDate);

    List<String> showUserExpensesCategory(UUID userId);

    void getExpensesBySpecificCategory(UUID userId, String category, LocalDate fromDate);

    void generateReport(UUID userId, LocalDate fromDate);
}
