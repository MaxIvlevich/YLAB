package org.example.homework_1.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Represents a user's wallet containing their balance, budget, and savings goals.
 * This class holds the user's current balance, their monthly budget, and any
 * savings goals they have set. It also keeps track of when the wallet was last updated.
 *
 */
@Data
public class Wallet {
    private Long userId;
    private double amount;
    private double monthlyBudget;
    private Map<String, BigDecimal> savingsGoals;
    private LocalDateTime lastUpdated;

    public Wallet(Long userId){
        this.userId = userId;
        this.amount = 0.0;
        this.monthlyBudget = 0.0;
        this.savingsGoals = new HashMap<>();
        this.lastUpdated = LocalDateTime.now();
    }

    public Wallet(Long userId, double amount, double monthlyBudget, Map<String, BigDecimal> savingsGoals) {
        this.userId = userId;
        this.amount = amount;
        this.monthlyBudget = monthlyBudget;
        this.savingsGoals = savingsGoals;
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

