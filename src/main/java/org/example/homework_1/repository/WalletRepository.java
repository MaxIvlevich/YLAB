package org.example.homework_1.repository;

import org.example.homework_1.models.Wallet;

import java.math.BigDecimal;
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
    public void addGoal(UUID userId, String goalName, BigDecimal targetAmount) {
        Wallet wallet = userWallets.get(userId);
        if (wallet != null) {
            wallet.addGoal(goalName, targetAmount);
        }
    }

    public Map<String,BigDecimal> getUserGoals(UUID userId){
        Wallet wallet = userWallets.get(userId);
        if (wallet != null) {
           return wallet.getSavingsGoals();
        }
        return null;

    }

    /**
     * The goal achievement marker method a specific user
     * @param userId unique user ID, UUID value
     * @param goalName Goal name, String value
     * @return  true if the goal is completed false otherwise
     */
    public boolean isGoalAchieved(UUID userId, String goalName,BigDecimal balance) {
        BigDecimal goal = userWallets.get(userId).getSavingsGoals().get(goalName);
        return (balance.compareTo(goal)>= 0);


    }

    /**
     * shows the user's current goals
     * @param userId unique user ID, UUID value
     */
    public void showGoals(UUID userId,BigDecimal balance) {
        System.out.println("Ваши цели накоплений:");
        for (Map.Entry<String, BigDecimal> goal : userWallets.get(userId).getSavingsGoals().entrySet()) {
            boolean achieved = isGoalAchieved(userId,goal.getKey(),balance);
            String status = achieved ? " Достигнута" : " Не достигнута";
            System.out.println("- " + goal.getKey() + ": " + goal.getValue() + " (" + status + ")");
        }







    }





}
