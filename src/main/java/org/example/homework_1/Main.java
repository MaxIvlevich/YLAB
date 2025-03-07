package org.example.homework_1;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.TransactionRepository;
import org.example.homework_1.repository.WalletRepository;
import org.example.homework_1.services.TransactionService;
import org.example.homework_1.services.UserService;
import org.example.homework_1.services.WalletService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static org.example.homework_1.models.enums.TransactionType.EXPENSE;
import static org.example.homework_1.models.enums.TransactionType.INCOME;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UserService userService = new UserService();
    private static final WalletRepository walletRepository = new WalletRepository();
    private static final TransactionRepository transactionRepository = new TransactionRepository();
    private static final TransactionService transactionService = new TransactionService(transactionRepository);
    private static final WalletService walletService = new WalletService(walletRepository, transactionRepository);
    private static User currentUser = null;
    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private static void showMainMenu() {
        System.out.println("\n==== Управление финансами ====");
        System.out.println("1 Пополнить баланс");
        System.out.println("2 Потратить деньги");
        System.out.println("3 Установить месячный бюджет");
        System.out.println("4 Добавить цель накопления");
        System.out.println("5 Проверить цели");
        System.out.println("6 Показать баланс");
        System.out.println("7 Показать бюджет");
        System.out.println("8 Меню транзакций");
        System.out.println("9 Настройки  пользователя");
        System.out.println("0 Выйти из аккаунта");
        System.out.print(" Выберите действие: ");

        int choice = scanner.nextInt();

        switch (choice) {
            case 1 -> {
                balanceOperation(INCOME);
            }
            case 2 -> {
                balanceOperation(EXPENSE);
            }
            case 3 -> {
                System.out.print("Введите сумму месячного бюджета: ");
                double budget = scanner.nextDouble();
                walletService.setBudget(currentUser.getUserId(), budget);
            }
            case 4 -> goalManager();
            case 5 -> walletService.showGoals(currentUser.getUserId());
            case 6 -> walletService.showBalance(currentUser.getUserId());
            case 7 -> walletService.showBudget(currentUser.getUserId());
            case 8 -> transactionMenu();
            case 9 -> userMenu();
            case 0 -> {
                currentUser = null; // Выход из аккаунта
                System.out.println("Вы вышли из аккаунта.");
            }
            default -> System.out.println(" Неверный ввод! Попробуйте снова.");
        }
    }

    private static void userMenu() {
        System.out.println("1 Изменить  Имя пользователя ");
        System.out.println("2 Изменить  email ");
        System.out.println("3 Изменить  Пароль ");
        System.out.println("4 удалить пользователя ");
        System.out.println("0 В начало");
        int choice = scanner.nextInt();
        switch (choice){
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
            case 4->{
                if(userService.deleteUser(currentUser.getUserId())){
                currentUser = null;
            }
            }

            case 0 ->  showMainMenu();
        }

    }
    public static void goalManager(){
        scanner.nextLine();
        System.out.print("Введите название цели: ");
        String goalName = scanner.nextLine();
        System.out.print("Введите сумму для накопления: ");
        double goalAmount = scanner.nextDouble();
        walletService.addGoal(currentUser.getUserId(), goalName, goalAmount);
        showMainMenu();
    }

    private static void transactionMenu(){
        System.out.println("1 Показать все транзакции ");
        System.out.println("2 выбрать транзакцию для редактирования ");
        System.out.println("3 выбрать транзакцию для Удаления  ");
        System.out.println("0 В начало");
        int choice = scanner.nextInt();
        switch (choice){
            case 1 ->  transactionService.showUserTransactions(currentUser.getUserId());
            case 2 ->  upgradeTransaction();
            case 3 ->  deleteTransaction();
            case 0 ->  showMainMenu();
        }
    }

    private static void deleteTransaction() {
        System.out.println("Выберете 1 транзакцию");
        List<Transaction> transactions = transactionService.showUserTransactions(currentUser.getUserId());
        int choice = scanner.nextInt();
        Transaction transaction = transactions.get(choice-1);
        transactionService.deleteTransaction(currentUser.getUserId(),transaction.getTransactionUUID());
        walletService.checkAndNotifyBudgetExceeded(currentUser.getUserId());

    }

    private static void upgradeTransaction() {
        scanner.nextLine();
        System.out.println("Выберете 1 транзакцию");
        List<Transaction> transactions = transactionService.showUserTransactions(currentUser.getUserId());
        int choice = scanner.nextInt();
        Transaction transaction = transactions.get(choice-1);
        System.out.println("Введите новую сумму");
        double newAmount = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Введите новую категорию");
        String newCategory = scanner.nextLine();
        System.out.println("Введите новое описание");
        String newDescription = scanner.nextLine();
        transaction.setAmount(newAmount);
        transaction.setCategory(newCategory);
        transaction.setDescription(newDescription);
        boolean updated = transactionService.updateTransaction(currentUser.getUserId(), transaction.getTransactionUUID(),transaction);
        walletService.checkAndNotifyBudgetExceeded(currentUser.getUserId());
        System.out.println(updated ? "Транзакция успешно обновлена." : "Транзакция не найдена.");
        System.out.println("Транзакции пользователя после обновления: " + transactionRepository.getUserTransactions(currentUser.getUserId()).size());
        System.out.println("Обновленная транзакция: " + transactionRepository.getUserTransactions(currentUser.getUserId()).get(0).getAmount());
    }

    private static void showAuthMenu() {
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
        private static void loginUser() {
            scanner.nextLine();
            System.out.print("Введите email: ");
            String email = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();
            Optional<User> currentUserOptional = userService.login(email, password);
            if(currentUserOptional.isEmpty()){
                showAuthMenu();
            }else {
                currentUser= currentUserOptional.get();
                walletService.createWalletForUser(currentUserOptional.get().getUserId());
            }
        }

        private static void registerUser() {
            scanner.nextLine();
            System.out.print("Введите имя: ");
            String name = scanner.nextLine();
            System.out.print("Введите email: ");
            String email = scanner.nextLine();
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine();
            userService.register(name,email, password);


        }
        private static void balanceOperation(TransactionType type){
            System.out.print(type == TransactionType.INCOME ? "Введите сумму пополнения: " : "Введите сумму расходов: ");
            double amount;
            do {
                while (!scanner.hasNextDouble()) {
                    System.out.println("Ошибка! Введите корректное число.");
                    scanner.next();
                }
                amount = scanner.nextDouble();
                scanner.nextLine(); // Очистка после nextDouble()

                if (amount <= 0) {
                    System.out.println("Ошибка! Сумма должна быть положительной.");
                }
            } while (amount <= 0);

            System.out.print("Введите категорию: ");
            String category = scanner.nextLine();
            System.out.print("Введите описание: ");
            String description = scanner.nextLine();
            if (type == TransactionType.INCOME) {
                transactionService.addTransaction(currentUser.getUserId(), TransactionType.INCOME, amount, category, description);

            } else {
                transactionService.addTransaction(currentUser.getUserId(), TransactionType.EXPENSE, amount, category, description);
            }
            walletService.showBalance(currentUser.getUserId());
            walletService.checkAndNotifyBudgetExceeded(currentUser.getUserId());



        }
    }






