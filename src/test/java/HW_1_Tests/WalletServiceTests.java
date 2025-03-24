package HW_1_Tests;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;
import org.example.homework_1.services.Interfaces.EmailServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.WalletServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTests {
    @Mock
    private WalletRepositoryInterface walletRepository;
    @Mock
    private TransactionRepositoryInterface transactionRepository;
    @Mock
    private EmailServiceInterface emailService;
    @Mock
    private UserServiceInterface userService;
    @InjectMocks
    private WalletServiceImpl walletService;

    private Long userId;

    @BeforeEach
    void setUp() {
        Long userId = 1L;
    }

    @Test
    void testCreateWalletForUser() {
        walletService.createWalletForUser(userId);
        verify(walletRepository, times(1)).initializeWallet(userId);
    }

    @Test
    void testGetBalance() {
        Long userId = 1L;
        Transaction incomeTransaction = new Transaction(
                userId, userId, TransactionType.INCOME, BigDecimal.valueOf(1000), "Salary", LocalDate.now(), "Salary payment"
        );
        Transaction expenseTransaction = new Transaction(
                userId , userId, TransactionType.EXPENSE, BigDecimal.valueOf(500), "Food", LocalDate.now(), "Grocery shopping"
        );

        when(transactionRepository.getUserTransactions(userId)).thenReturn(Arrays.asList(incomeTransaction, expenseTransaction));
        BigDecimal expectedBalance = BigDecimal.valueOf(500.0);
        BigDecimal actualBalance = walletService.getBalance(userId);
        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    void testSetBudget() {
        double budget = 5000.0;
        walletService.setBudget(userId, budget);
        verify(walletRepository, times(1)).setBudget(userId, budget);
    }

    @Test
    void testIsBudgetExceeded_BudgetExceeded() {
        double userBudget = 1000;
        Transaction transaction1 = new Transaction(userId , userId,
                TransactionType.EXPENSE, BigDecimal.valueOf(600), "", LocalDate.now(), "Food");
        Transaction transaction2 = new Transaction(userId, userId,
                TransactionType.EXPENSE, BigDecimal.valueOf(1400), "", LocalDate.now(), "Entertainment");
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.getUserExpenseTransactions(userId)).thenReturn(transactions);
        when(walletService.getBudget(userId)).thenReturn(userBudget);
        when(userService.getUserEmail(userId)).thenReturn("user@example.com");

        boolean result = walletService.isBudgetExceeded(userId);
        assertTrue(result);
        verify(emailService).sendEmail(eq("user@example.com"), eq("Превышен лимит Бюджета"), contains("Бюджет превышен"));
        verify(transactionRepository).getUserExpenseTransactions(userId);
    }

    @Test
    void testIsBudgetExceeded_BudgetNotExceeded() {

        double userBudget = 1500.0;
        Transaction transaction1 = new Transaction(userId, userId,
                TransactionType.EXPENSE, BigDecimal.valueOf(600), "", LocalDate.now(), "Food");
        Transaction transaction2 = new Transaction(userId, userId,
                TransactionType.EXPENSE, BigDecimal.valueOf(400), "", LocalDate.now(), "Entertainment");
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.getUserExpenseTransactions(userId)).thenReturn(transactions);
        when(walletService.getBudget(userId)).thenReturn(userBudget);

        boolean result = walletService.isBudgetExceeded(userId);
        assertFalse(result);
        verify(emailService, times(0)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testAddGoal() {
        Long userId = 1L;
        String goalName = "Vacation Fund";
        BigDecimal targetAmount = BigDecimal.valueOf(5000);
        doNothing().when(walletRepository).addGoal(userId, goalName, targetAmount);
        walletService.addGoal(userId, goalName, targetAmount);
        verify(walletRepository, times(1)).addGoal(userId, goalName, targetAmount);

    }

    @Test
    void testCheckGoal_GoalAchieved() {
        Long userId = 1L;
        String goalName = "Vacation Fund";
        BigDecimal balance = BigDecimal.valueOf(5000);
        BigDecimal goal = BigDecimal.valueOf(5000);

        when(walletRepository.getUserGoals(userId)).thenReturn(Map.of(goalName, goal)); // Возвращаем карту с целями
        when(walletRepository.isGoalAchieved(userId, goalName, balance)).thenReturn(true); // Проверка, что цель достигнута


        System.setOut(new java.io.PrintStream(System.out));
        walletService.checkGoal(userId, goalName, balance);

        verify(walletRepository, times(1)).getUserGoals(userId);
        verify(walletRepository, times(1)).isGoalAchieved(userId, goalName, balance);
    }

    @Test
    void testCheckGoal_GoalNotAchieved() {

        String goalName = "Vacation Fund";
        BigDecimal balance = BigDecimal.valueOf(3000);
        BigDecimal goal = BigDecimal.valueOf(5000);

        when(walletRepository.getUserGoals(userId)).thenReturn(Map.of(goalName, goal)); // Возвращаем карту с целями
        when(walletRepository.isGoalAchieved(userId, goalName, balance)).thenReturn(false); // Проверка, что цель не достигнута

        walletService.checkGoal(userId, goalName, balance);


        verify(walletRepository, times(1)).getUserGoals(userId);
        verify(walletRepository, times(1)).isGoalAchieved(userId, goalName, balance);
    }


}
