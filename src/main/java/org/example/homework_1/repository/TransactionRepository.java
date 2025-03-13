package org.example.homework_1.repository;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;

import java.util.*;
/**
 * Repository class responsible for managing transactions.
 * This class interacts with the underlying data storage to add, update, delete, and retrieve transactions for users.
 * It provides methods to get all transactions, get transactions by user, and perform other related operations.
 *
 */
public class TransactionRepository implements TransactionRepositoryInterface {
    private final Map<UUID, List<Transaction>> transactions = new HashMap<>();
    /**
     * Adds a new transaction to the user's transaction list.
     *
     * This method adds a given transaction to the list of transactions for the user. If the user does not yet have any
     * transactions, a new list is created and the transaction is added to it. The transaction is stored in a map, with the
     * user's UUID as the key and a list of transactions as the value.
     *
     * @param transaction The {@link Transaction} object to be added. This should contain the details of the transaction
     *                   such as the user UUID, amount, type, description, and any other relevant information.
     */
    @Override
    public void addTransaction(Transaction transaction) {
        transactions.putIfAbsent(transaction.getUserUUID(), new ArrayList<>());
        transactions.get(transaction.getUserUUID()).add(transaction);

    }

    /**
     * Retrieves all transactions for a given user.
     * This method returns a list of all transactions associated with the specified user. If the user has no transactions,
     * an empty list is returned.
     *
     * @param userId The UUID of the user whose transactions are to be retrieved.
     * @return A list of {@link Transaction} objects representing all transactions for the user.
     *         If the user has no transactions, an empty list is returned.
     */
    @Override
    public List<Transaction> getUserTransactions(UUID userId) {

        return transactions.getOrDefault(userId, new ArrayList<>());

    }

    /**
     * Retrieves a list of expense transactions for a given user.
     * This method filters through the user's transactions and returns a list of transactions that are of type
     * {@link TransactionType#EXPENSE}. If the user has no expense transactions, an empty list is returned.
     *
     * @param userId The UUID of the user whose expense transactions are to be retrieved.
     * @return A list of {@link Transaction} objects representing the user's expense transactions.
     *         If no expense transactions are found, an empty list is returned.
     */
    @Override
    public List<Transaction> getUserExpenseTransactions(UUID userId) {
        List<Transaction> expenseTransactions = new ArrayList<>();
        List<Transaction> userTransactions = transactions.get(userId);
        if (userTransactions != null) {
            for (Transaction transaction : userTransactions) {
                if (transaction.getType() == TransactionType.EXPENSE) {
                    expenseTransactions.add(transaction);
                }
            }
        }
        return expenseTransactions;
    }
    /**
     * Deletes a specific transaction for a given user based on the transaction ID.
     * This method searches for a transaction by its unique transaction ID within the user's list of transactions.
     * If the transaction is found, it is removed from the list.
     *
     * @param uuid The UUID of the user whose transaction is to be deleted.
     * @param transactionId The UUID of the transaction to be deleted.
     * @return {@code true} if the transaction was found and successfully deleted, {@code false} if the transaction does not exist or could not be deleted.
     */
    @Override
    public boolean deleteTransaction(UUID uuid, UUID transactionId) {
        List<Transaction> userTransactions = transactions.get(uuid);
        if (userTransactions != null) {
            System.out.println("Транзакция удалена " + transactionId);
            return userTransactions.removeIf(t -> t.getTransactionUUID().equals(transactionId));

        }
        return false;
    }
    /**
     * Upgrades an existing transaction for a given user by replacing it with an updated transaction.
     *
     * This method searches for an existing transaction by its unique transaction ID, and if found,
     * it updates the transaction with the provided `updatedTransaction` details.
     *
     * @param userId The UUID of the user whose transaction is to be upgraded.
     * @param transactionId The UUID of the transaction to be updated.
     * @param updatedTransaction The transaction object containing the updated details.
     * @return true if the transaction was found and successfully updated, {@code false} if the transaction does not exist for the user.
     */
    @Override
    public boolean upgradeTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction) {
        List<Transaction> userTransactions = transactions.get(userId);
        if (userTransactions != null) {
            for (int i = 0; i < userTransactions.size(); i++) {
                Transaction existingTransaction = userTransactions.get(i);
                if (existingTransaction.getTransactionUUID().equals(transactionId)) {
                    userTransactions.set(i, updatedTransaction);
                    System.out.println("Транзакция обновлена для пользователя: " + userId);
                    return true;
                }
            }
        }
        return false;

    }
}
