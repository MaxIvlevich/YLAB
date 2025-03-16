package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryJDBC implements TransactionRepositoryInterface {

    private final Connection connection ;
    public TransactionRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO app.transactions (id, user_id, type, amount, category, date, description) " +
                "VALUES (nextval('app.transactions_id_seq'), ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, transaction.getUserUUID());
            statement.setString(2, transaction.getType().toString());
            statement.setBigDecimal(3, transaction.getAmount());
            statement.setString(4, transaction.getCategory());
            statement.setDate(5, Date.valueOf(transaction.getDate()));
            statement.setString(6, transaction.getDescription());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @Override
    public List<Transaction> getUserTransactions(Long userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM app.transactions WHERE user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction transaction = mapRowToTransaction(resultSet);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    @Override
    public List<Transaction> getUserExpenseTransactions(Long userId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM app.transactions WHERE user_id = ? AND type = 'EXPENSE'";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Transaction transaction = mapRowToTransaction(resultSet);
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception properly in a real application
        }
        return transactions;
    }

    @Override
    public boolean deleteTransaction(Long userId, Long transactionId) {
        String sql = "DELETE FROM app.transactions WHERE id = ? AND user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, transactionId); // Используем long для transactionId
            statement.setLong(2, userId); // Используем long для userId
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception properly in a real application
            return false;
        }
    }
    @Override
    public boolean upgradeTransaction(Long userId, Long transactionId, Transaction updatedTransaction) {
        String sql = "UPDATE app.transactions SET type = ?, amount = ?, category = ?, date = ?, description = ? " +
                "WHERE id = ? AND user_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, updatedTransaction.getType().toString());
            statement.setBigDecimal(2, updatedTransaction.getAmount());
            statement.setString(3, updatedTransaction.getCategory());
            statement.setDate(4, Date.valueOf(updatedTransaction.getDate()));
            statement.setString(5, updatedTransaction.getDescription());
            statement.setLong(6, transactionId); // Используем long для transactionId
            statement.setLong(7, userId); // Используем long для userId

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception properly in a real application
            return false;
        }
    }
    private Transaction mapRowToTransaction(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id"); // Используем long для поля id
        long userId = resultSet.getLong("user_id"); // Используем long для user_id
        String type = resultSet.getString("type");
        BigDecimal amount = resultSet.getBigDecimal("amount");
        String category = resultSet.getString("category");
        Date date = resultSet.getDate("date");
        String description = resultSet.getString("description");

        Transaction transaction = new Transaction();
        transaction.setTransactionUUID(id);
        transaction.setUserUUID(userId);
        transaction.setType(TransactionType.valueOf(type));
        transaction.setAmount(amount);
        transaction.setCategory(category);
        transaction.setDate(date.toLocalDate());
        transaction.setDescription(description);

        return transaction;
    }
}
