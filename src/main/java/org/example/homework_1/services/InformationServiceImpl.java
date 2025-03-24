package org.example.homework_1.services;

import org.example.homework_1.services.Interfaces.InformationServiceInterface;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A class for displaying various user information
 */
public class InformationServiceImpl implements InformationServiceInterface {

    private final TransactionServiceInterface transactionService;
   private final WalletServiceInterface walletService;

    public InformationServiceImpl(TransactionServiceInterface transactionService, WalletServiceInterface walletService) {
        this.transactionService = transactionService;
        this.walletService = walletService;
    }

    /**
     * Generates a financial report for a user, summarizing their income, expenses, balance, and goals for a given period.
     *
     * @param userId  The unique identifier of the user for whom the report is generated.
     * @param fromDate The starting date of the period for which the financial report is generated.
     */
    @Override
    public void generateReport(Long userId, LocalDate fromDate) {

        double totalIncome = transactionService.getTotalIncome(userId, fromDate);
        double totalExpenses = transactionService.getTotalExpenses(userId, fromDate);
        BigDecimal currentBalance = walletService.getBalance(userId);
        double balanceForPeriod = totalIncome - totalExpenses;

        System.out.println("Финансовый отчёт пользователя:");

        System.out.println("Период: с " + fromDate + " по " + LocalDate.now());
        System.out.println("--------------------------------------------------");
        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общие расходы: " + totalExpenses);
        System.out.println("Текущий баланс: " + currentBalance);
        System.out.println("Ваш баланс за выбранный период: " + balanceForPeriod);
        System.out.println("--------------------------------------------------");
        System.out.println("Бюджет");
        walletService.showBudget(userId);

        System.out.println("--------------------------------------------------");
        System.out.println(" Анализ расходов по категориям:");
        transactionService.getExpensesByCategory(userId, fromDate);

        System.out.println("--------------------------------------------------");
        System.out.println(" Цели:");
        walletService.showGoals(userId);

        System.out.println("--------------------------------------------------");


    }
}
