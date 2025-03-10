package org.example.homework_1.services.Interfaces;

import java.math.BigDecimal;
import java.util.UUID;

public interface WalletServiceInterface {
    void createWalletForUser(UUID userId);

    void showBalance(UUID userId);

    BigDecimal getBalance(UUID userId);

    void setBudget(UUID userId, double budget);

    void showBudget(UUID userId);

    boolean isBudgetExceeded(UUID userId);

    double getBudget(UUID userId);

    void checkAndNotifyBudgetExceeded(UUID userId);

    void addGoal(UUID userId, String goalName, BigDecimal targetAmount);

    void showGoals(UUID userId);

    void checkGoal(UUID userId, String goalName, BigDecimal balance);

    void checkAllGoals(UUID userId);
}
