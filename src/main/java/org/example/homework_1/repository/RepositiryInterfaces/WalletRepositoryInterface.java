package org.example.homework_1.repository.RepositiryInterfaces;

import org.example.homework_1.models.Goal;
import org.example.homework_1.models.Wallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WalletRepositoryInterface {
    void initializeWallet(Long userId);

    void setBudget(Long userId, BigDecimal budget);

    double getBudget(Long userId);

    void addGoal(Long userId, String goalName, BigDecimal targetAmount);

    List<Goal> getUserGoals(Long userId);

    boolean isGoalAchieved(Long userId, String goalName, BigDecimal balance);

    void showGoals(Long userId, BigDecimal balance);

    Wallet getUserWallet(Long userId);


}
