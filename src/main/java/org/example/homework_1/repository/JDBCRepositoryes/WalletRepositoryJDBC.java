package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.models.Wallet;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class WalletRepositoryJDBC implements WalletRepositoryInterface {

    private final Connection connection;

    public WalletRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void initializeWallet(Long userId) {
        String sql = "INSERT INTO app.wallets (user_id, balance, monthly_budget) VALUES (?, 0, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setBudget(Long userId, double budget) {
        String sql = "UPDATE app.wallets SET monthly_budget = ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, budget);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public double getBudget(Long userId) {
        String sql = "SELECT monthly_budget FROM app.wallets WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("monthly_budget");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    @Override
    public void addGoal(Long userId, String goalName, BigDecimal targetAmount) {
        String sql = "INSERT INTO app.goals (user_id, goal_name, target_amount) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, goalName);
            stmt.setBigDecimal(3, targetAmount);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Map<String, BigDecimal> getUserGoals(Long userId) {
        Map<String, BigDecimal> goals = new HashMap<>();
        String sql = "SELECT goal_name, target_amount FROM app.goals WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.put(rs.getString("goal_name"), rs.getBigDecimal("target_amount"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }
    @Override
    public boolean isGoalAchieved(Long userId, String goalName, BigDecimal balance) {
        String sql = "SELECT target_amount FROM app.goals WHERE user_id = ? AND goal_name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setString(2, goalName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal targetAmount = rs.getBigDecimal("target_amount");
                    return balance.compareTo(targetAmount) >= 0; // Сравниваем баланс с целевой суммой
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public void showGoals(Long userId, BigDecimal balance) {
        Map<String, BigDecimal> goals = getUserGoals(userId);
        if (goals.isEmpty()) {
            System.out.println("No goals set for this user.");
        } else {
            for (Map.Entry<String, BigDecimal> entry : goals.entrySet()) {
                String goalName = entry.getKey();
                BigDecimal targetAmount = entry.getValue();
                boolean achieved = balance.compareTo(targetAmount) >= 0;
                System.out.printf("Goal: %s, Target: %s, Achieved: %b%n", goalName, targetAmount, achieved);
            }
        }
    }
    @Override
    public Wallet getUserWallet(Long userId) {
        String sql = "SELECT balance, monthly_budget FROM app.wallets WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    BigDecimal monthlyBudget = rs.getBigDecimal("monthly_budget");
                    Map<String, BigDecimal> goals = getUserGoals(userId);
                    return new Wallet(userId, balance.doubleValue(), monthlyBudget.doubleValue(), goals);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
