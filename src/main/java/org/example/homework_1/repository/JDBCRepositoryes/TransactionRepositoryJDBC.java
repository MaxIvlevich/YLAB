package org.example.homework_1.repository.JDBCRepositoryes;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * {@code TransactionRepositoryJDBC} is a repository class that implements the {@link TransactionRepositoryInterface}.
 * It provides database operations related to {@link Transaction} objects using JDBC.
 *
 * <p>This class interacts with the underlying database to perform CRUD (Create, Read, Update, Delete) operations
 * on transaction records in the database. It requires a {@link Connection} object to execute SQL queries and updates.
 * All the methods in this class assume that the connection provided is valid and active.
 */
public class TransactionRepositoryJDBC implements TransactionRepositoryInterface {

    private final Connection connection ;
    public TransactionRepositoryJDBC(Connection connection) {
        this.connection = connection;
    }
    /**
     * Adds a new transaction to the database.
     *
     * <p>This method inserts a new record into the {@code app.transactions} table.
     * The transaction ID is automatically generated using the sequence {@code app.transactions_id_seq}.
     *
     * @param transaction the {@link Transaction} object containing the transaction details
     */
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
    /**
     * Retrieves all transactions for a specific user.
     *
     * <p>This method queries the {@code app.transactions} table and returns a list of transactions
     * associated with the given user ID.
     *
     * @param userId the ID of the user whose transactions should be retrieved
     * @return a list of {@link Transaction} objects belonging to the specified user;
     *         returns an empty list if no transactions are found
     */
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
    /**
     * Retrieves all expense transactions for a specific user.
     *
     * <p>This method queries the {@code app.transactions} table and returns a list of transactions
     * where the {@code type} is {@code 'EXPENSE'} for the given user ID.
     *
     * @param userId the ID of the user whose expense transactions should be retrieved
     * @return a list of {@link Transaction} objects representing expenses;
     *         returns an empty list if no expense transactions are found
     */
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
            e.printStackTrace();
        }
        return transactions;
    }
    /**
     * Deletes a transaction for a given user.
     *
     * <p>This method removes a transaction from the {@code app.transactions} table
     * based on the provided transaction ID and user ID. The deletion will only
     * be successful if the transaction belongs to the specified user.
     *
     * @param userId       the ID of the user who owns the transaction
     * @param transactionId the ID of the transaction to be deleted
     * @return {@code true} if the transaction was successfully deleted,
     *         {@code false} if no transaction was found or an error occurred
     */
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
    /**
     * Updates an existing transaction for a given user.
     *
     * <p>This method modifies an existing transaction in the {@code app.transactions} table
     * based on the provided transaction ID and user ID. If the transaction exists
     * and belongs to the specified user, it will be updated with the new details.
     *
     * @param userId           the ID of the user who owns the transaction
     * @param transactionId    the ID of the transaction to be updated
     * @param updatedTransaction the transaction object containing the updated details
     * @return {@code true} if the transaction was successfully updated,
     *         {@code false} if no transaction was found or an error occurred
     */
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

    @Override
    public Transaction getTransactionBuId(Long id) {
        Transaction transaction = new Transaction() ;
        String sql = "SELECT * FROM app.transactions WHERE id = ? ";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                 transaction = mapRowToTransaction(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transaction;
    }

        /**
     * Maps a row from the {@code ResultSet} to a {@link Transaction} object.
     *
     * <p>This method converts a row from the {@code ResultSet} into a {@link Transaction} object.
     * It extracts the values from the corresponding columns in the result set and sets them into
     * a new {@link Transaction} instance. The fields are mapped from the result set's column names,
     * including {@code id}, {@code user_id}, {@code type}, {@code amount}, {@code category},
     * {@code date}, and {@code description}.
     *
     * @param resultSet the {@code ResultSet} containing the transaction data
     * @return a {@link Transaction} object populated with the data from the result set
     * @throws SQLException if a database access error occurs or the result set contains invalid data
     */
    private Transaction mapRowToTransaction(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("id");
        long userId = resultSet.getLong("user_id");
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
