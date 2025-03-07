package org.example.homework_1.repository;

import org.example.homework_1.models.Wallet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * a repository for wallet management
 */
public class WalletRepository {
    private final Map<UUID, Wallet> userWallets = new HashMap<>();

    /**
     *the method that initializes the wallet for the new user
     * @param userId unique user ID,UUID value
     */
    public void initializeWallet(UUID userId) {
        userWallets.put(userId, new Wallet(userId));
    }

    /**
     * Sets a monthly budget
     * @param userId unique user ID, UUID value
     * @param budget monthly limit ,double value
     */
    public void setBudget(UUID userId, double budget) {
        Wallet balance = userWallets.get(userId);
        if (balance != null) {
            balance.setMonthlyBudget(budget);
        }
    }


    /**
     * getting the user's budget
     * @param userId unique user ID, UUID value
     * @return current budget,double value
     */
    public double getBudget(UUID userId) {
        return userWallets.getOrDefault(userId, new Wallet(userId)).getMonthlyBudget();
    }

    /**
     * Adds a goal for accumulation
     * @param userId unique user ID, UUID value
     * @param goalName Goal name, String value
     * @param targetAmount goal amount, double value
     */
    public void addGoal(UUID userId, String goalName, double targetAmount) {
        Wallet balance = userWallets.get(userId);
        if (balance != null) {
            balance.addGoal(goalName, targetAmount);
        }
    }

    /**
     * The goal achievement marker method a specific user
     * @param userId unique user ID, UUID value
     * @param goalName Goal name, String value
     * @return  true if the goal is completed false otherwise
     */
    public boolean isGoalAchieved(UUID userId, String goalName) {
        Wallet wallet = userWallets.get(userId);
        return wallet != null && wallet.isGoalAchieved(goalName);
    }

    /**
     * shows the user's current goals
     * @param userId unique user ID, UUID value
     */
    public void showGoals(UUID userId) {
        Wallet wallet = userWallets.get(userId);
        if (wallet != null) {
            wallet.showGoals();
        }
    }





}
