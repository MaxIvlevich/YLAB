package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.models.Goal;
import org.example.homework_1.models.Wallet;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;

import java.math.BigDecimal;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Implementation of the {@link WalletRepositoryInterface} that interacts with the database using JDBC.
 *
 * <p>This class is responsible for performing operations related to user wallets in the database.
 */
public class WalletRepositoryJDBC implements WalletRepositoryInterface {

    private final Connection connection;

    public WalletRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }
    /**
     * Initializes a new wallet for a user with the given user ID.
     * The wallet is created with an initial balance and monthly budget of 0, and the current timestamp is recorded
     * for the last update.
     *
     * @param userId the ID of the user for whom the wallet is being initialized
     */
    @Override
    public void initializeWallet(Long userId) {
        String sql = "INSERT INTO app.wallets (user_id, balance, monthly_budget,last_updated) VALUES (?, 0, 0,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.from(Instant.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets a new monthly budget for a user in the wallet.
     *
     * <p>This method updates the monthly budget for a user in the database based on the provided user ID.
     * The new budget value is stored in the corresponding user's wallet record.
     *
     * @param userId the ID of the user whose monthly budget is to be updated
     * @param budget the new monthly budget value to be set for the user
     */
    @Override
    public void setBudget(Long userId, BigDecimal budget) {
        String sql = "UPDATE app.wallets SET monthly_budget = ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, budget);
            stmt.setLong(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Retrieves the monthly budget of a user's wallet.
     *
     * <p>This method queries the database to fetch the current monthly budget for a given user by their ID.
     * If the user has a wallet record, the budget value is returned; otherwise, a default value of 0 is returned.
     *
     * @param userId the ID of the user whose monthly budget is to be retrieved
     * @return the monthly budget of the user, or 0 if no record is found for the user
     */
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
    /**
     * Adds a new financial goal for a user.
     *
     * <p>This method inserts a new record into the goals table with the given user ID, goal name, and target amount.
     * The goal is added for the user, allowing them to track their progress toward reaching the target amount.
     *
     * @param userId the ID of the user for whom the goal is being created
     * @param goalName the name of the financial goal (e.g., "Buy a car", "Save for vacation")
     * @param targetAmount the target amount for the goal (e.g., $5000)
     */
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
    /**
     * Retrieves the goals for a user.
     *
     * <p>This method queries the database for all financial goals associated with the specified user ID,
     * returning a map where the keys are the goal names and the values are the corresponding target amounts.
     *
     * @param userId the ID of the user whose goals are to be retrieved
     * @return a map where the key is the goal name and the value is the target amount for that goal (e.g.,
     *         {"Buy a car": 5000, "Save for vacation": 2000})
     */
    @Override
    public List<Goal> getUserGoals(Long userId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM app.goals WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    goals.add(new Goal(
                            rs.getLong("id"),
                            rs.getLong("user_id"),
                            rs.getString("goal_name"),
                            rs.getBigDecimal("target_amount")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goals;
    }
    /**
     * Checks if a user's goal has been achieved based on the current balance.
     *
     * <p>This method queries the database for the target amount of a specific goal of a user and compares
     * it with the current balance. If the balance is greater than or equal to the target amount, the goal is
     * considered achieved.
     *
     * @param userId the ID of the user whose goal is to be checked
     * @param goalName the name of the goal to be checked
     * @param balance the current balance of the user
     * @return true if the user's balance is greater than or equal to the target amount for the goal, false otherwise
     */
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
    /**
     * Displays the user's goals along with their target amounts and whether the goal has been achieved based on the current balance.
     *
     * <p>This method retrieves all goals for a specified user and compares each goal's target amount with the user's current balance.
     * It then prints the goal name, target amount, and a boolean indicating whether the goal has been achieved (i.e., if the balance is greater than or equal to the target amount).
     *
     * @param userId the ID of the user whose goals are to be displayed
     * @param balance the current balance of the user, which will be compared with the goal's target amount
     */
    @Override
    public void showGoals(Long userId, BigDecimal balance) {
        List<Goal> goals = getUserGoals(userId);
        if (goals.isEmpty()) {
            System.out.println("No goals set for this user.");
        } else {
            for (Goal goal : goals) {
                String goalName = goal.getGoalName();
                BigDecimal targetAmount = goal.getTarget();
                boolean achieved = balance.compareTo(targetAmount) >= 0;
                System.out.printf("Goal: %s, Target: %s, Achieved: %b%n", goalName, targetAmount, achieved);
            }
        }
    }
    /**
     * Retrieves the user's wallet information, including balance, monthly budget, and associated goals.
     *
     * <p>This method queries the database to fetch the user's wallet details such as balance and monthly budget.
     * It then retrieves the user's goals and constructs a {@link Wallet} object containing the user's financial data.
     * If the user does not have an associated wallet or the data cannot be fetched, the method returns null.
     *
     * @param userId the ID of the user whose wallet information is to be retrieved
     * @return a {@link Wallet} object containing the user's balance, monthly budget, and goals if found, or null if no wallet data is available for the given user
     */
    @Override
    public Wallet getUserWallet(Long userId) {
        String sql = "SELECT balance, monthly_budget FROM app.wallets WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal balance = rs.getBigDecimal("balance");
                    BigDecimal monthlyBudget = rs.getBigDecimal("monthly_budget");
                    List<Goal> goals = getUserGoals(userId);
                    return new Wallet(userId, balance, monthlyBudget, goals);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
