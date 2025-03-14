package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.models.Wallet;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class WalletRepositoryJDBC implements WalletRepositoryInterface {
    @Override
    public void initializeWallet(UUID userId) {

    }

    @Override
    public void setBudget(UUID userId, double budget) {

    }

    @Override
    public double getBudget(UUID userId) {
        return 0;
    }

    @Override
    public void addGoal(UUID userId, String goalName, BigDecimal targetAmount) {

    }

    @Override
    public Map<String, BigDecimal> getUserGoals(UUID userId) {
        return null;
    }

    @Override
    public boolean isGoalAchieved(UUID userId, String goalName, BigDecimal balance) {
        return false;
    }

    @Override
    public void showGoals(UUID userId, BigDecimal balance) {

    }

    @Override
    public Wallet getUserWallet(UUID userId) {
        return null;
    }
}
