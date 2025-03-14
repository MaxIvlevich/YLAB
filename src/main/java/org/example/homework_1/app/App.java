package org.example.homework_1.app;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;
import org.example.homework_1.repository.RepositoryInMap.TransactionRepository;
import org.example.homework_1.repository.RepositoryInMap.UserRepository;
import org.example.homework_1.repository.RepositoryInMap.WalletRepository;
import org.example.homework_1.services.*;
import org.example.homework_1.services.Interfaces.*;
import org.example.homework_1.util.StringKeeper;
import org.example.homework_1.util.interfaces.StringKeeperInterface;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.example.homework_1.models.enums.TransactionType.EXPENSE;
import static org.example.homework_1.models.enums.TransactionType.INCOME;

public class App {
   //private final Map<UUID, List<Transaction>> transactions = new HashMap<>();
   //private final Map<UUID, User> users = new HashMap<>();
   //private final Map<UUID, Wallet> userWallets = new HashMap<>();
    private  final Scanner scanner = new Scanner(System.in);
    private final UserRepositoryInterface userRepositoryInterface = new UserRepository();
    private  final UserServiceInterface userService = new UserServiceImpl(userRepositoryInterface);
    private  final StringKeeperInterface stringKeeperInter = new StringKeeper();
    private  final EmailServiceInterface emailService = new EmailService();
    private  final WalletRepositoryInterface walletRepository = new WalletRepository();
    private  final TransactionRepositoryInterface transactionRepository = new TransactionRepository();
    private  final WalletServiceInterface walletService = new WalletServiceImpl(walletRepository, transactionRepository, emailService, userService);
    private  final TransactionServiceInterface transactionService = new TransactionService(transactionRepository);
    private  final InformationServiceInterface informationService = new InformationServiceImpl(transactionService, walletService);
    private  User currentUser = null;
    private  UUID userId = null;
    public void startApp(){
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }


    public  void showMainMenu() {
        stringKeeperInter.printMenu();
        int choice = isCorrectChoice();
        switch (choice) {
            case 1 -> financeOperation();

            case 2 -> analysisFinance();

            case 3 -> {
                System.out.print("Введите сумму месячного бюджета: ");
                double budget = scanner.nextDouble();
                walletService.setBudget(userId, budget);
            }
            case 4 -> goalManager();
            case 5 -> walletService.showGoals(userId);
            case 6 -> walletService.showBudget(userId);
            case 7 -> walletService.showBalance(userId);
            case 8 -> transactionMenu();
            case 9 -> {
                if (currentUser.getRoles().equals(Roles.ROLE_ADMIN)) {
                    adminMenu();
                } else userMenu();
            }
            case 0 -> {
                currentUser = null; // Выход из аккаунта
                System.out.println("Вы вышли из аккаунта.");
            }
            default -> System.out.println(" Неверный ввод! Попробуйте снова.");
        }
    }

    private void adminMenu() {
        System.out.println("---Выберите пользовыателя---");
        List<User> users = userService.showAllUsers();
        System.out.println("0 Назад");
        int choice = isCorrectChoice();
        if (choice == 0) {
            showMainMenu();
        } else {
            userManage(users, choice);
        }

    }

    private  void userManage(List<User> users, int choice) {
        User user = users.get(choice - 1);
        System.out.println(" Выбран пользователь " + user.getName());
        System.out.println("1 Забанить");
        System.out.println("2 Удалить");
        System.out.println("3 Посмотреть транзакции");
        System.out.println("0 В Начало ");
        int choiceFunction = isCorrectChoice();
        switch (choiceFunction) {
            case 1 -> {
                user.setStatus(Status.STATUS_BANNED);
                userService.updateUser(user);
            }
            case 2 -> {
                userService.deleteUser(user.getUserId());

            }
            case 3 -> {
                transactionService.showUserTransactions(user.getUserId());
            }
            case 4 -> showMainMenu();
        }

    }

    private  void analysisFinance() {
        System.out.println("1 Расчёт суммарного дохода и расхода  за опредеоенный срок ");
        System.out.println("2 Анализ расходов по категориям");
        System.out.println("3 Оичет по финансовому состоянию");
        System.out.println("0 В начало");
        int choice = isCorrectChoice();
        switch (choice) {
            case 1 -> printTotalIncomeAndExpense();
            case 2 -> analyzeExpensesByCategory();
            case 3 -> generateFinancialReport();
            case 0 -> showMainMenu();
        }
    }

    private  void generateFinancialReport() {
        LocalDate today = LocalDate.now();
        informationService.generateReport(userId, today.minusMonths(1));

    }

    private  void analyzeExpensesByCategory() {
        LocalDate today = LocalDate.now();
        System.out.println("1 Расходы по всем категориям за месяц");
        System.out.println("2 Расходы по определенной категории");
        System.out.println("В начало");
        int choice = isCorrectChoice();
        switch (choice) {
            case 1 -> transactionService.getExpensesByCategory(userId, today.minusMonths(1));
            case 2 -> analyzeExpensesByUserCategory();
            case 0 -> showMainMenu();
        }
    }

    private  void analyzeExpensesByUserCategory() {
        LocalDate today = LocalDate.now();
        System.out.println("Выберите категорию ");
        List<String> categories = transactionService.showUserExpensesCategory(userId);
        int choice = isCorrectChoice();
        String category = categories.get(choice - 1);
        transactionService.getExpensesBySpecificCategory(userId, category, today.minusMonths(1));

    }

    private  void printTotalIncomeAndExpense() {
        LocalDate today = LocalDate.now();
        System.out.println("1 за день");
        System.out.println("2 за неделю");
        System.out.println("3 за месяц");
        int choice = isCorrectChoice();
        switch (choice) {
            case 1 -> transactionService.getTotalExpensesOrIncomeForPeriod(userId, today);
            case 2 -> transactionService.getTotalExpensesOrIncomeForPeriod(userId, today.minusWeeks(1));
            case 3 -> transactionService.getTotalExpensesOrIncomeForPeriod(userId, today.minusMonths(1));
            case 0 -> showMainMenu();
        }
    }

    private  void userMenu() {
        stringKeeperInter.printUserMenu();
        int choice = scanner.nextInt();
        switch (choice) {
            case 1 -> {
                scanner.nextLine();
                System.out.println("Введите новое имя ");
                String newName = scanner.nextLine();
                currentUser.setName(newName);
                userService.updateUser(currentUser);
            }
            case 2 -> {
                scanner.nextLine();
                System.out.println("Введите новый Email");
                String newEmail = scanner.nextLine();
                currentUser.setEmail(newEmail);
                userService.updateUser(currentUser);
            }
            case 3 -> {
                scanner.nextLine();
                System.out.println("Ввелите новый пароль ");
                String newPassword = scanner.nextLine();
                currentUser.setPassword(newPassword);
                userService.updateUser(currentUser);

            }
            case 4 -> {
                if (userService.deleteUser(userId)) {
                    currentUser = null;
                }
            }
            case 5 -> {
                currentUser.setRoles(Roles.ROLE_ADMIN);
                userService.updateUser(currentUser);
            }

            case 0 -> showMainMenu();
        }

    }

    private  void goalManager() {
        scanner.nextLine();
        System.out.print("Введите название цели: ");
        String goalName = scanner.nextLine();
        System.out.print("Введите сумму для накопления: ");
        BigDecimal amount = isCorrectIncomeDouble();
        walletService.addGoal(userId, goalName, amount);
        showMainMenu();
    }

    private  void transactionMenu() {
        System.out.println("1 Показать все транзакции ");
        System.out.println("2 выбрать транзакцию для редактирования ");
        System.out.println("3 выбрать транзакцию для Удаления  ");
        System.out.println("0 В начало");
        int choice = isCorrectChoice();
        switch (choice) {
            case 1 -> transactionService.showUserTransactions(userId);
            case 2 -> upgradeTransaction();
            case 3 -> deleteTransaction();
            case 0 -> showMainMenu();
        }
    }

    private  void deleteTransaction() {
        System.out.println("Выберете 1 транзакцию");
        List<Transaction> transactions = transactionService.showUserTransactions(userId);
        int choice = isCorrectChoice();
        Transaction transaction = transactions.get(choice - 1);
        transactionService.deleteTransaction(userId, transaction.getTransactionUUID());
        walletService.checkAndNotifyBudgetExceeded(userId);
        walletService.checkAllGoals(userId);

    }

    private  void upgradeTransaction() {
        scanner.nextLine();
        System.out.println("Выберете 1 транзакцию");
        List<Transaction> transactions = transactionService.showUserTransactions(userId);
        int choice = isCorrectChoice();
        Transaction transaction = transactions.get(choice - 1);
        System.out.println("Введите новую сумму");
        BigDecimal newAmount = scanner.nextBigDecimal();
        scanner.nextLine();
        System.out.println("Введите новую категорию");
        String newCategory = scanner.nextLine();
        System.out.println("Введите новое описание");
        String newDescription = scanner.nextLine();
        transaction.setAmount(newAmount);
        transaction.setCategory(newCategory);
        transaction.setDescription(newDescription);
        boolean updated = transactionService.updateTransaction(userId, transaction.getTransactionUUID(), transaction);
        walletService.checkAndNotifyBudgetExceeded(userId);
        walletService.checkAllGoals(userId);
        System.out.println(updated ? "Транзакция успешно обновлена." : "Транзакция не найдена.");
        System.out.println("Транзакции пользователя после обновления: " + transactionRepository.getUserTransactions(currentUser.getUserId()).size());
        System.out.println("Обновленная транзакция: " + transactionRepository.getUserTransactions(currentUser.getUserId()).get(0).getAmount());
    }

    private  void showAuthMenu() {
        System.out.println("\n==== Авторизация ====");
        System.out.println("1 Регистрация");
        System.out.println("2 Вход");
        System.out.println("0 Выход");
        System.out.print(" Выберите действие: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> registerUser();
            case 2 -> loginUser();
            case 0 -> {
                System.out.println("До свидания!");
                System.exit(0);
            }
            default -> System.out.println("Неверный ввод! Попробуйте снова.");
        }
    }

    private  void loginUser() {
        scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        Optional<User> currentUserOptional = userService.login(email, password);
        if (currentUserOptional.isEmpty()) {
            showAuthMenu();
        } else {
            currentUser = currentUserOptional.get();
            userId = currentUser.getUserId();
            walletService.createWalletForUser(currentUserOptional.get().getUserId());
        }
    }

    private  void registerUser() {
        scanner.nextLine();
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();
        userService.register(name, email, password);

    }

    private  void balanceOperation(TransactionType type) {
        System.out.print(type == TransactionType.INCOME ? "Введите сумму пополнения: " : "Введите сумму расходов: ");
        BigDecimal amount = isCorrectIncomeDouble();
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine();
        System.out.print("Введите описание: ");
        String description = scanner.nextLine();
        if (type == TransactionType.INCOME) {
            transactionService.addTransaction(userId, TransactionType.INCOME, amount, category, description);

        } else {
            transactionService.addTransaction(userId, TransactionType.EXPENSE, amount, category, description);
        }
        walletService.showBalance(userId);
        walletService.checkAndNotifyBudgetExceeded(userId);
        walletService.checkAllGoals(userId);


    }
    private  void financeOperation() {
        System.out.println("1 Пополнение счета");
        System.out.println("2 Потратить средства");
        int choice = isCorrectChoice();
        switch (choice) {
            case 1 -> balanceOperation(INCOME);
            case 2 -> balanceOperation(EXPENSE);

        }
    }

    private  int isCorrectChoice() {
        while (!scanner.hasNextInt()) { // Пока ввод не число, просим ввести снова
            System.out.println(" Ошибка! Введите корректное число.");
            scanner.next(); // Очищаем некорректный ввод
        }
        return scanner.nextInt();
    }

    private  BigDecimal isCorrectIncomeDouble() {
        BigDecimal amount;
        BigDecimal z = new BigDecimal(0);
        do {
            while (!scanner.hasNextDouble()) {
                System.out.println("Ошибка! Введите корректное число.");
                scanner.next();
            }
            amount = scanner.nextBigDecimal();
            scanner.nextLine(); // Очистка после nextDouble()

            if (amount.compareTo(z) <= 0) {
                System.out.println("Ошибка! Сумма должна быть положительной.");
            }
        } while (amount.compareTo(z) <= 0);
        return amount;

    }
}
