package org.example.homework_1.models;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A wallet model with a balance, monthly limit, and savings
 */
@Data
public class Wallet {
    private UUID userId;
    private double amount;
    private double monthlyBudget;
    private Map<String, Double> savingsGoals;
    private LocalDateTime lastUpdated;

    public Wallet(UUID userId){
        this.userId = userId;
        this.amount = 0.0;
        this.monthlyBudget = 0.0;
        this.savingsGoals = new HashMap<>();
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * sets the balance
     * @param amount the amount to be added to the balance, double value
     */
    public void setAmount(double amount) {
        this.amount = amount;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * returns the budget for the month
     * @return  budget for the month, double value
     */
    public double getMonthlyBudget() {
        return monthlyBudget;
    }

    /**
     * the amount of the monthly budget
     * @param monthlyBudget the amount of the monthly budget
     */
    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    /**
     * adds a new goal
     * @param goalName the name of the goal for accumulation
     * @param targetAmount amount to accumulate
     */
    public void addGoal(String goalName, double targetAmount) {
        savingsGoals.put(goalName, targetAmount);
    }

    /**
     * The goal achievement marker method
     * @param goalName the name of the goal for accumulation
     * @return true if the goal is completed false otherwise
     */
    public boolean isGoalAchieved(String goalName) {
        return amount >= savingsGoals.get(goalName);
    }

    /**
     * shows current goals and their status
     */
    public void showGoals() {
        System.out.println("Ваши цели накоплений:");
        for (Map.Entry<String, Double> goal : savingsGoals.entrySet()) {
            boolean achieved = isGoalAchieved(goal.getKey());
            String status = achieved ? " Достигнута" : " Не достигнута";
            System.out.println("- " + goal.getKey() + ": " + goal.getValue() + " (" + status + ")");
        }
    }
}

