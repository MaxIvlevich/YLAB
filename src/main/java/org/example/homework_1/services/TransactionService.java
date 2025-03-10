package org.example.homework_1.services;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.TransactionRepository;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class TransactionService implements TransactionServiceInterface {
    private final TransactionRepository transactionRepository;
    private final WalletServiceImpl walletService;
    LocalDate now = LocalDate.now();

    public TransactionService(TransactionRepository transactionRepository, WalletServiceImpl walletService) {
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Override
    public void addTransaction(UUID userId, TransactionType type, BigDecimal amount, String category, String description) {
        UUID id = UUID.randomUUID();
        Transaction transaction = new Transaction(id,userId,type, amount, category, now, description);
        transactionRepository.addTransaction(transaction);
        System.out.println("Транзакция добавлена.");

    }
    @Override
    public List<Transaction> showUserTransactions(UUID userId) {
        List<Transaction> transactions = transactionRepository.getUserTransactions(userId);
        if (transactions.isEmpty()) {
            System.out.println(" У вас нет записей.");
        }
        int index = 1;
        for (Transaction t : transactions) {
            System.out.println(index +". "+ t.getType() + ": " + t.getAmount() + " |" + t.getCategory() + " | " + t.getDescription());
            index++;
        }
        return transactions;
    }
    @Override
    public boolean updateTransaction(UUID userId, UUID transactionId, Transaction updatedTransaction){
        return transactionRepository.upgradeTransaction(userId, transactionId, updatedTransaction);
        }


    @Override
    public void deleteTransaction(UUID userId, UUID transactionId) {
        System.out.println("Удаление транзакции " + transactionId);
        transactionRepository.deleteTransaction(userId, transactionId);
    }

    @Override
    public double getTotalIncome(UUID userId, LocalDate fromDate) {
        return transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.INCOME)
                .filter(transaction -> !transaction.getDate().isBefore(fromDate)) // Дата >= fromDate
                .mapToDouble(transaction->transaction.getAmount().doubleValue())
                .sum();
    }
    @Override
    public double getTotalExpenses(UUID userId, LocalDate fromDate) {
        return transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .filter(transaction -> !transaction.getDate().isBefore(fromDate)) // Дата >= fromDate
                .mapToDouble(transaction->transaction.getAmount().doubleValue())
                .sum();
    }

    @Override
    public void getTotalExpensesOrIncomeForPeriod(UUID userId, LocalDate fromDate){
        System.out.println("Ваш расход за выбранный период | " +  getTotalExpenses(userId,fromDate));
        System.out.println("Ваш доход за выбранный период  | " + getTotalIncome(userId,fromDate) );

    }
    @Override
    public  Map<String, Double> getExpensesByCategory(UUID userId, LocalDate fromDate) {
        Map<String, Double> expensesByCategory = transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE) // Только расходы
                .filter(transaction -> !transaction.getDate().isBefore(fromDate)) // Дата >= fromDate
                .collect(Collectors.groupingBy(
                        Transaction::getCategory, // Группируем по категории
                        Collectors.summingDouble(transaction->transaction.getAmount().doubleValue()) // Суммируем сумму расходов
                ));

        System.out.println("Расходы по категориям с " + fromDate + ":");
        expensesByCategory.forEach((category, totalAmount) -> {
            System.out.printf("Категория: %-15s | Сумма: %.2f%n", category, totalAmount);
        });
        return expensesByCategory;
    }

    @Override
    public List<String> showUserExpensesCategory(UUID userId) {
        Set<String> userExpensesCategory = transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE)
                .map(Transaction::getCategory)
                .collect(Collectors.toSet());
        int i = 1;
        for (String category : userExpensesCategory) {
            System.out.println(i +".  "+ category);
            i++;
        }
        return userExpensesCategory.stream().toList();
    }

    @Override
    public void getExpensesBySpecificCategory(UUID userId, String category, LocalDate fromDate) {
      double sumExpense = transactionRepository.getUserTransactions(userId).stream()
                .filter(transaction -> transaction.getType() == TransactionType.EXPENSE) // Только расходы
                .filter(transaction -> transaction.getCategory().equalsIgnoreCase(category)) // Фильтр по категории
                .filter(transaction -> !transaction.getDate().isBefore(fromDate)) // Дата >= fromDate
                .mapToDouble(transaction-> transaction.getAmount().doubleValue())
                .sum();

        System.out.println("Сумма расходов по категории: "+ category +"| "+ sumExpense);
    }

    @Override
    public void generateReport(UUID userId, LocalDate fromDate){

        double totalIncome = getTotalIncome(userId, fromDate);
        double totalExpenses = getTotalExpenses(userId, fromDate);
        BigDecimal currentBalance = walletService.getBalance(userId);
        double balanceForPeriod = totalIncome - totalExpenses;

        System.out.println("Финансовый отчёт пользователя:" );

        System.out.println("Период: с " + fromDate + " по " + LocalDate.now());
        System.out.println("--------------------------------------------------");
        System.out.println("Общий доход: " + totalIncome);
        System.out.println("Общие расходы: " + totalExpenses);
        System.out.println("Текущий баланс: " + currentBalance);
        System.out.println("Ваш баланс за выбранный период: "+ balanceForPeriod);
        System.out.println("--------------------------------------------------");
        System.out.println("Бюджет");
        walletService.showBudget(userId);

        System.out.println("--------------------------------------------------");
        System.out.println(" Анализ расходов по категориям:");
        getExpensesByCategory(userId,fromDate);

        System.out.println("--------------------------------------------------");
        System.out.println(" Цели:");
        walletService.showGoals(userId);

        System.out.println("--------------------------------------------------");




    }

}
