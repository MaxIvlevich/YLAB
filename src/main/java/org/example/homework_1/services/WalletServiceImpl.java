package org.example.homework_1.services;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;
import org.example.homework_1.services.Interfaces.EmailServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Service for working with user wallets
 */
public class WalletServiceImpl implements WalletServiceInterface {
    private final WalletRepositoryInterface walletRepository;
    private final TransactionRepositoryInterface transactionRepository;
    private final EmailServiceInterface emailService;
    private final UserServiceInterface userService;

    public WalletServiceImpl(WalletRepositoryInterface walletRepository,
                             TransactionRepositoryInterface transactionRepository,
                             EmailServiceInterface emailService,
                             UserServiceInterface userService) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.emailService = emailService;
        this.userService = userService;
    }

    /**
     * The method that creates a wallet for a new user
     *
     * @param userId unique user ID, UUID value
     */
    @Override
    public void createWalletForUser(Long userId) {
        walletRepository.initializeWallet(userId);
        showBalance(userId);
    }

    /**
     * shows the user's current balance
     *
     * @param userId unique user ID ,UUID value
     */
    @Override
    public void showBalance(Long userId) {
        System.out.println("Ваш текущий баланс: " + getBalance(userId));
    }

    /**
     * a method for obtaining the user's balance by a specified parameter
     *
     * @param userId unique user ID,the parameter for the search,UUID value
     * @return current balance,double value
     */
    @Override
    public BigDecimal getBalance(Long userId) {
        double balance = transactionRepository.getUserTransactions(userId).stream()
                .mapToDouble(t -> t.getType() == TransactionType.INCOME ?
                        t.getAmount().doubleValue() :
                        t.getAmount().negate().doubleValue())
                .sum();
        return BigDecimal.valueOf(balance);
    }

    /**
     * The method for setting the monthly limit
     *
     * @param userId unique user ID
     * @param budget monthly limit ,double value
     */
    @Override
    public void setBudget(Long userId, double budget) {
        walletRepository.setBudget(userId, budget);
        System.out.println("Бюджет установлен: " + budget);
    }

    /**
     * getting the user's budget
     *
     * @param userId unique user ID, UUID value
     */
    @Override
    public void showBudget(Long userId) {
        if (walletRepository.getBudget(userId) > 0) {
            System.out.println("Ваш месячный бюджет: " + walletRepository.getBudget(userId));
        } else {
            System.out.println("Ваш месячный бюджет не установлен ");
        }
    }

    /**
     * a marker that tracks budget overflows
     *
     * @param userId unique user ID, UUID value
     * @return true if the budget is exceeded and false otherwise
     */
    @Override
    public boolean isBudgetExceeded(Long userId) {
        List<Transaction> transactions = transactionRepository.getUserExpenseTransactions(userId);
        double userBudget = getBudget(userId);
        LocalDate currentDate = LocalDate.now();
        LocalDate oneMonthAgo = currentDate.minus(1, ChronoUnit.MONTHS);
        double sum = transactions.stream()
                .filter(transaction -> !transaction.getDate().isBefore(oneMonthAgo)) // Отфильтровываем транзакции за последний месяц
                .mapToDouble(transaction -> transaction.getAmount().doubleValue()).sum();
        if (sum > userBudget && userBudget != 0.0) {
            String body = "Бюджет превышен на : " + (sum - userBudget);
            String subject = "Превышен лимит Бюджета";
            System.out.println(body);
            emailService.sendEmail(userService.getUserEmail(userId), subject, body);
            return true;
        }
        return false;

    }

    /**
     * a method for getting a budget,or the value was not set, it will return 0.0.
     *
     * @param userId unique user ID, UUID value
     * @return returns the budget value from the repository
     */
    @Override
    public double getBudget(Long userId) {
        return walletRepository.getBudget(userId);

    }

    /**
     * budget excess marker
     *
     * @param userId unique user ID, UUID value
     */
    @Override
    public void checkAndNotifyBudgetExceeded(Long userId) {
        if (isBudgetExceeded(userId)) {
            System.out.println("Внимание! Превышен бюджет для пользователя: " + userId);
        }
    }

    /**
     * Adds a goal for accumulation
     *
     * @param userId       unique user ID, UUID value
     * @param goalName     Goal name, String value
     * @param targetAmount goal amount, double value
     */
    @Override
    public void addGoal(Long userId, String goalName, BigDecimal targetAmount) {
        walletRepository.addGoal(userId, goalName, targetAmount);
        System.out.println("Цель '" + goalName + "' добавлена! Требуется накопить: " + targetAmount);
    }

    /**
     * shows the user's current goals
     *
     * @param userId unique user ID, UUID value
     */
    @Override
    public void showGoals(Long userId) {
        BigDecimal balance = getBalance(userId);
        walletRepository.showGoals(userId, balance);
    }

    /**
     * checks whether the goal has been completed by name.
     *
     * @param userId   unique user ID, UUID value
     * @param goalName name of the user's goal String  value
     * @param balance  the user's current balance BigDecimal value.
     */
    @Override
    public void checkGoal(Long userId, String goalName, BigDecimal balance) {
        BigDecimal goal = walletRepository.getUserGoals(userId).get(goalName);
        if (walletRepository.isGoalAchieved(userId, goalName, balance)) {
            System.out.println("Цель: " + goalName + " достигнута" + "| " + goal + "/" + balance + "|");
        }
    }

    /**
     * they will check all the user's goals
     *
     * @param userId unique user ID, UUID value
     */
    @Override
    public void checkAllGoals(Long userId) {
        BigDecimal balance = getBalance(userId);
        List<String> goalNames = walletRepository.getUserGoals(userId).keySet().stream().toList();
        for (String goalName : goalNames) {
            checkGoal(userId, goalName, balance);
        }
    }
}
