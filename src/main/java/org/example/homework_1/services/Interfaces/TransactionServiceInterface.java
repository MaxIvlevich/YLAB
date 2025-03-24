package org.example.homework_1.services.Interfaces;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TransactionServiceInterface {
    void addTransaction(Long userId, TransactionType type, BigDecimal amount, String category, String description);

    List<Transaction> showUserTransactions(Long userId);

    boolean updateTransaction(Long userId, Long transactionId, Transaction updatedTransaction);

    boolean deleteTransaction(Long userId, Long transactionId);

    double getTotalIncome(Long userId, LocalDate fromDate);

    double getTotalExpenses(Long userId, LocalDate fromDate);

    void getTotalExpensesOrIncomeForPeriod(Long userId, LocalDate fromDate);

    Map<String, Double> getExpensesByCategory(Long userId, LocalDate fromDate);

    List<String> showUserExpensesCategory(Long userId);

    void getExpensesBySpecificCategory(Long userId, String category, LocalDate fromDate);
    Transaction getTransactionById(Long transactionId);


}
