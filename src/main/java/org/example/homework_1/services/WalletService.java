package org.example.homework_1.services;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.TransactionRepository;
import org.example.homework_1.repository.WalletRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Service for working with user wallets
 */
public class WalletService {
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * The method that creates a wallet for a new user
     * @param userId unique user ID, UUID value
     */
    public void createWalletForUser(UUID userId) {
        walletRepository.initializeWallet(userId);
        showBalance(userId);
    }

    /**
     * shows the user's current balance
     * @param userId unique user ID ,UUID value
     */
    public void showBalance(UUID userId) {
        System.out.println("Ваш текущий баланс: " + getBalance(userId));
    }

    /**
     * a method for obtaining the user's balance by a specified parameter
     * @param userId unique user ID,the parameter for the search,UUID value
     * @return current balance,double value
     */
    public double getBalance(UUID userId) {
        return transactionRepository.getUserTransactions(userId).stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ? t.getAmount() : -t.getAmount())
                .sum();
    }

    /**
     * The method for setting the monthly limit
     * @param userId unique user ID
     * @param budget monthly limit ,double value
     */
    public void setBudget(UUID userId, double budget) {
        walletRepository.setBudget(userId, budget);
        System.out.println("Бюджет установлен: " + budget);
    }

    /**
     * getting the user's budget
     * @param userId unique user ID, UUID value
     */
    public void showBudget(UUID userId) {
        System.out.println("аш месячный бюджет: " + walletRepository.getBudget(userId));
    }

    /**
     * a marker that tracks budget overflows
     * @param userId unique user ID, UUID value
     * @return true if the budget is exceeded and false otherwise
     */
    public boolean isBudgetExceeded(UUID userId){
       List<Transaction> transactions =  transactionRepository.getUserExpenseTransactions(userId);
       double userBudget = getBudget(userId);
       LocalDate currentDate = LocalDate.now();
       LocalDate oneMonthAgo = currentDate.minus(1, ChronoUnit.MONTHS);
        double sum  =  transactions.stream()
                .filter(transaction -> !transaction.getDate().isBefore(oneMonthAgo)) // Отфильтровываем транзакции за последний месяц
                .mapToDouble(Transaction::getAmount).sum();
        if (sum > userBudget){
            System.out.println("Бюджет превышен на : " +  (sum-userBudget));
            return true;
        }
        return false;


    }

    /**
     * a method for getting a budget,or the value was not set, it will return 0.0.
     * @param userId unique user ID, UUID value
     * @return returns the budget value from the repository
     */
    public double getBudget(UUID userId){
        return walletRepository.getBudget(userId);

    }

    /**
     * budget excess marker
     * @param userId unique user ID, UUID value
     */
    public void checkAndNotifyBudgetExceeded(UUID userId) {
        if (isBudgetExceeded(userId)) {
            System.out.println("Внимание! Превышен бюджет для пользователя: " + userId);
        }
    }

    /**
     * Adds a goal for accumulation
     * @param userId unique user ID, UUID value
     * @param goalName Goal name, String value
     * @param targetAmount goal amount, double value
     */
    public void addGoal(UUID userId, String goalName, double targetAmount) {
        walletRepository.addGoal(userId, goalName, targetAmount);
        System.out.println("Цель '" + goalName + "' добавлена! Требуется накопить: " + targetAmount);
    }

    /**
     * shows the user's current goals
     * @param userId unique user ID, UUID value
     */
    public void showGoals(UUID userId) {
        walletRepository.showGoals(userId);
    }

    public void checkGoal(){


    }


}
