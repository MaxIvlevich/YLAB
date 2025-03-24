package org.example.homework_1.services.Interfaces;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletServiceInterface {
    void createWalletForUser(Long userId);

    void showBalance(Long userId);

    BigDecimal getBalance(Long userId);

    void setBudget(Long userId, double budget);

    void showBudget(Long userId);

    boolean isBudgetExceeded(Long userId);

    double getBudget(Long userId);

    void checkAndNotifyBudgetExceeded(Long userId);

    void addGoal(Long userId, String goalName, BigDecimal targetAmount);

    void showGoals(Long userId);

    void checkGoal(Long userId, String goalName, BigDecimal balance);

    void checkAllGoals(Long userId);
}
