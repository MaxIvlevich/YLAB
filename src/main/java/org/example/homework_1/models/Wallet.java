package org.example.homework_1.models;

import liquibase.pro.packaged.L;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Represents a user's wallet containing their balance, budget, and savings goals.
 * This class holds the user's current balance, their monthly budget, and any
 * savings goals they have set. It also keeps track of when the wallet was last updated.
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Wallet {
    private Long userId;
    private BigDecimal amount;
    private BigDecimal monthlyBudget;
    private List<Goal> savingsGoals;
    private LocalDateTime lastUpdated;

    public Wallet(Long userId){
        this.userId = userId;
        this.amount = null;
        this.monthlyBudget = null;
        this.savingsGoals = new ArrayList<>();
        this.lastUpdated = LocalDateTime.now();
    }

    public Wallet(Long userId, BigDecimal amount, BigDecimal monthlyBudget, List<Goal> savingsGoals) {
        this.userId = userId;
        this.amount = amount;
        this.monthlyBudget = monthlyBudget;
        this.savingsGoals = savingsGoals;
    }

    /**
     * sets the balance
     * @param amount the amount to be added to the balance, double value
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        this.lastUpdated = LocalDateTime.now();
    }

    /**
     * returns the budget for the month
     * @return  budget for the month, double value
     */
    public BigDecimal getMonthlyBudget() {
        return monthlyBudget;
    }

    /**
     * the amount of the monthly budget
     * @param monthlyBudget the amount of the monthly budget
     */
    public void setMonthlyBudget(BigDecimal monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }





}

