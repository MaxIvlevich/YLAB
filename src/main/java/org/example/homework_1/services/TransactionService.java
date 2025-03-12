package org.example.homework_1.services;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A service for working with user transactions
 */
public class TransactionService implements TransactionServiceInterface {
    private final TransactionRepositoryInterface transactionRepository;


    public TransactionService(TransactionRepositoryInterface transactionRepository) {
        this.transactionRepository = transactionRepository;

    }

    /**
     * Adds a new transaction to the system.
     *
     * @param userId is the UUID of the user who owns the transaction.
     * @param type Transaction type (income or expense).
     * @param amount Transaction amount.
     * @param category Transaction category (for example, "Products", "Salary").
     * @param description Transaction description.
     */
    @Override
    public void addTransaction(UUID userId, TransactionType type, BigDecimal amount, String category, String description) {
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(id, userId, type, amount, category, LocalDate.now(), description);
        transactionRepository.addTransaction(transaction);
        System.out.println("Транзакция добавлена.");

    }
    /**
     * Displays and returns a list of user transactions.
     * If the user has no transactions, a message is printed to the console.
     *
     * @param userId UUID of the user whose transactions should be retrieved.
     * @return A list of the user's transactions.
     */
    @Override
    public List<Transaction> showUserTransactions(UUID userId) {
        List<Transaction> transactions = transactionRepository.getUserTransactions(userId);
        if (transactions.isEmpty()) {
            System.out.println(" У вас нет записей.");
        }
        int index = 1;
        for (Transaction t : transactions) {
            System.out.println(index + ". " + t.getType() + ": " + t.getAmount() + " |" + t.getCategory() + " | " + t.getDescription());
            index++;
        }
        return transactions;
    }
    /**
     * Updates an existing transaction for a specific user.
     *
     * @param userId           UUID of the user who owns the transaction.
     * @param transactionId    UUID of the transaction to be updated.
     * @param updatedTransaction The new transaction data to replace the existing one.
     * @return true if the transaction was successfully updated, false otherwise.
     */
    @Override
    public boolean updateTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction) {
        return transactionRepository.upgradeTransaction(userId, transactionId, updatedTransaction);
    }

    /**
     * Deletes a specific transaction for a given user.
     *
     * @param userId        UUID of the user who owns the transaction.
     * @param transactionId UUID of the transaction to be deleted.
     */
    @Override
    public void deleteTransaction(UUID userId, UUID transactionId) {
        transactionRepository.deleteTransaction(userId, transactionId);
        System.out.println("Транзакция удалена " + transactionId);
    }
    /**
     * Calculates the total income of a user from a specified date.
     *
     * @param userId   UUID of the user whose income should be calculated.
     * @param fromDate The starting date from which income transactions are considered.
     * @return The total income amount as a double.
     */
    @Override
    public double getTotalIncome(UUID userId, LocalDate fromDate) {
        return transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.INCOME)
                .filter(transaction -> !transaction.getDate().isBefore(fromDate))
                .mapToDouble(transaction -> transaction.getAmount().doubleValue())
                .sum();
    }
    /**
     * Calculates the total expenses of a user from a specified date.
     *
     * @param userId   UUID of the user whose expenses should be calculated.
     * @param fromDate The starting date from which expense transactions are considered.
     * @return The total expense amount as a double.
     */
    @Override
    public double getTotalExpenses(UUID userId, LocalDate fromDate) {
        return transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .filter(transaction -> !transaction.getDate().isBefore(fromDate))
                .mapToDouble(transaction -> transaction.getAmount().doubleValue())
                .sum();
    }
    /**
     * Displays the total expenses and income for a user within a specified period.
     *
     * @param userId   UUID of the user whose expenses and income are being calculated.
     * @param fromDate The starting date from which transactions are considered.
     */
    @Override
    public void getTotalExpensesOrIncomeForPeriod(UUID userId, LocalDate fromDate) {
        System.out.println("Ваш расход за выбранный период | " + getTotalExpenses(userId, fromDate));
        System.out.println("Ваш доход за выбранный период  | " + getTotalIncome(userId, fromDate));

    }
    /**
     * Retrieves and displays the total expenses grouped by category for a user within a specified period.
     *
     * @param userId   UUID of the user whose expenses are being calculated.
     * @param fromDate The starting date from which expense transactions are considered.
     * @return A map where the key is the category name, and the value is the total expense amount for that category.
     */
    @Override
    public Map<String, Double> getExpensesByCategory(UUID userId, LocalDate fromDate) {
        Map<String, Double> expensesByCategory = transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .filter(transaction -> !transaction.getDate().isBefore(fromDate))
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.summingDouble(transaction -> transaction.getAmount().doubleValue())
                ));

        System.out.println("Расходы по категориям с " + fromDate + ":");
        expensesByCategory.forEach((category, totalAmount) -> {
            System.out.printf("Категория: %-15s | Сумма: %.2f%n", category, totalAmount);
        });
        return expensesByCategory;
    }
    /**
     * Displays the list of unique expense categories for a user.
     *
     * @param userId UUID of the user whose expense categories are being retrieved.
     * @return A list of unique expense categories for the user.
     */
    @Override
    public List<String> showUserExpensesCategory(UUID userId) {
        Set<String> userExpensesCategory = transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .map(Transaction::getCategory)
                .collect(Collectors.toSet());
        int i = 1;
        for (String category : userExpensesCategory) {
            System.out.println(i + ".  " + category);
            i++;
        }
        return userExpensesCategory.stream().toList();
    }
    /**
     * Calculates and displays the total expenses for a specific category within a given period for a user.
     *
     * @param userId   UUID of the user whose expenses are being calculated.
     * @param category The category for which expenses are being calculated.
     * @param fromDate The starting date from which transactions are considered.
     */
    @Override
    public void getExpensesBySpecificCategory(UUID userId, String category, LocalDate fromDate) {
        double sumExpense = transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .filter(transaction -> transaction.getCategory().equalsIgnoreCase(category))
                .filter(transaction -> !transaction.getDate().isBefore(fromDate))
                .mapToDouble(transaction -> transaction.getAmount().doubleValue())
                .sum();

        System.out.println("Сумма расходов по категории: " + category + "| " + sumExpense);
    }

}
