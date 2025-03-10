package org.example.homework_1.models;

import lombok.Data;

import java.math.BigDecimal;
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
    private Map<String, BigDecimal> savingsGoals;
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
    public void addGoal(String goalName, BigDecimal targetAmount) {
        savingsGoals.put(goalName, targetAmount);
    }


}

