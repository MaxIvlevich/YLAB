package org.example.homework_1.repository.RepositiryInterfaces;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public interface WalletRepositoryInterface {
    void initializeWallet(UUID userId);

    void setBudget(UUID userId, double budget);

    double getBudget(UUID userId);

    void addGoal(UUID userId, String goalName, BigDecimal targetAmount);

    Map<String, BigDecimal> getUserGoals(UUID userId);

    boolean isGoalAchieved(UUID userId, String goalName, BigDecimal balance);

    void showGoals(UUID userId, BigDecimal balance);
}
