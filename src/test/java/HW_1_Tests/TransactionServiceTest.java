package HW_1_Tests;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    private TransactionRepositoryInterface transactionRepository;
    private TransactionService transactionService;
    private Long userId;
    private Long transaction1_Id;
    private Long transaction2_Id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String category;
    private String description;
    private Transaction transaction1;
    private Transaction transaction2;
    private List<Transaction> transactions;


    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();


    @BeforeEach
    void setUp() {


        System.setOut(new PrintStream(outContent));


        transactionRepository = Mockito.mock(TransactionRepositoryInterface.class);
        transactionService = new TransactionService(transactionRepository);

        userId =1L;
        transaction1_Id = 1L;
        transaction2_Id = 2L;
        transactionType = TransactionType.INCOME;
        amount = BigDecimal.valueOf(100);
        category = "Salary";
        description = "Monthly salary";

        transaction1 = new Transaction(transaction1_Id, userId, TransactionType.INCOME, BigDecimal.valueOf(100),
                "Salary", LocalDate.now(), "Monthly salary");
        transaction2 = new Transaction(transaction2_Id, userId, TransactionType.EXPENSE, BigDecimal.valueOf(50),
                "Food", LocalDate.now(), "Lunch");

        transactions = Arrays.asList(transaction1,transaction2);

    }

    @Test
    public void addTransaction_ShouldAddTransaction() {

        transactionService.addTransaction(userId, transactionType, amount, category, description);

        verify(transactionRepository, times(1)).addTransaction(Mockito.any(Transaction.class));

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).addTransaction(transactionCaptor.capture());

        Transaction capturedTransaction = transactionCaptor.getValue();

        assertEquals(userId, capturedTransaction.getUserUUID());
        assertEquals(transactionType, capturedTransaction.getType());
        assertEquals(amount, capturedTransaction.getAmount());
        assertEquals(category, capturedTransaction.getCategory());
        assertEquals(description, capturedTransaction.getDescription());
    }


    @Test
    public void showUserTransactions_WhenNoTransactions_ShouldPrintNoRecordsMessage() {

        when(transactionRepository.getUserTransactions(userId)).thenReturn(List.of());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        transactionService.showUserTransactions(userId);

        assertTrue(outputStream.toString().contains("У вас нет записей."));
    }

    @Test
    public void showUserTransactions_WhenTransactionsExist_ShouldPrintTransactions() {

        when(transactionRepository.getUserTransactions(userId)).thenReturn(Arrays.asList(transaction1, transaction2));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        transactionService.showUserTransactions(userId);

        assertTrue(outputStream.toString().contains("1. INCOME: 100 |Salary | Monthly salary"),
                "Output does not contain expected transaction 1. Actual output: " + outputStream.toString());
        assertTrue(outputStream.toString().contains("2. EXPENSE: 50 |Food | Lunch"),
                "Output does not contain expected transaction 2. Actual output: " + outputStream.toString());
    }

    @Test
    public void deleteTransaction_ShouldDeleteTransactionAndPrintMessage() {
        when(transactionRepository.deleteTransaction(userId, transaction1_Id)).thenReturn(true);
        boolean result = transactionService.deleteTransaction(userId, transaction1_Id);
        assertThat(result).isTrue();
        verify(transactionRepository, times(1)).deleteTransaction(userId, transaction1_Id);
    }

    @Test
    void shouldUpdateTransactionWhenTransactionExists() {
        when(transactionRepository.upgradeTransaction(userId, transaction1_Id, transaction2)).thenReturn(true);
        boolean result = transactionService.updateTransaction(userId, transaction1_Id, transaction2);
        assertThat(result).isTrue();
        verify(transactionRepository, times(1)).upgradeTransaction(userId, transaction1_Id, transaction2);
    }
    @Test
    void shouldNotUpdateTransactionWhenTransactionDoesNotExist() {
        when(transactionRepository.upgradeTransaction(userId, transaction1_Id, transaction2)).thenReturn(false);
        boolean result = transactionService.updateTransaction(userId, transaction1_Id, transaction2);
        assertThat(result).isFalse();
        verify(transactionRepository, times(0)).upgradeTransaction(userId, transaction1_Id, transaction1);
    }


    @Test
    void shouldReturnTotalIncomeAfterDate() {
        // Настроим мок репозитория для возвращения списка с транзакциями
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);
        double totalIncome = transactionService.getTotalIncome(userId, LocalDate.of(2025, 3, 1));
        assertThat(totalIncome).isEqualTo(100);
    }

    @Test
    void shouldReturnZeroIncomeIfNoIncomeTransactions() {
        List<Transaction> transactions = Collections.singletonList(transaction2);
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);
        double totalIncome = transactionService.getTotalIncome(userId, LocalDate.of(2025, 3, 1));
        assertThat(totalIncome).isEqualTo(0.0);
    }

    @Test
    void shouldReturnZeroIncomeIfTransactionsBeforeDate() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);
        double totalIncome = transactionService.getTotalIncome(userId, LocalDate.of(2025, 8, 15));
        assertThat(totalIncome).isEqualTo(0.0);
    }

    @Test
    void shouldReturnTotalIncomeWithEmptyTransactionList() {
        when(transactionRepository.getUserTransactions(userId)).thenReturn(Arrays.asList());
        double totalIncome = transactionService.getTotalIncome(userId, LocalDate.of(2025, 3, 1));
        assertThat(totalIncome).isEqualTo(0.0);
    }
    @Test
    void shouldReturnTotalExpensesAfterDate() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);
        double totalExpenses = transactionService.getTotalExpenses(userId, LocalDate.of(2025, 3, 1));
        assertThat(totalExpenses).isEqualTo(50);
    }

    @Test
    void shouldReturnZeroExpensesIfNoExpenseTransactions() {
        List<Transaction> transactions = Arrays.asList(transaction1);
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);
        double totalExpenses = transactionService.getTotalExpenses(userId, LocalDate.of(2025, 3, 1));
        assertThat(totalExpenses).isEqualTo(0.0);
    }

    @Test
    void shouldReturnZeroExpensesIfTransactionsBeforeDate() {
        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);

        double totalExpenses = transactionService.getTotalExpenses(userId, LocalDate.of(2025, 3, 15));

        assertThat(totalExpenses).isEqualTo(0.0);
    }

    @Test
    void shouldReturnTotalExpensesWithEmptyTransactionList() {
        when(transactionRepository.getUserTransactions(userId)).thenReturn(Arrays.asList());
        double totalExpenses = transactionService.getTotalExpenses(userId, LocalDate.of(2025, 3, 1));
        assertThat(totalExpenses).isEqualTo(0.0);
    }
    @Test
    void shouldReturnCorrectExpensesByCategory() {
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);

        Map<String, Double> result = transactionService.getExpensesByCategory(userId, LocalDate.of(2025, 3, 1));

        assertThat(result).containsEntry("Food", 50.00);
        assertThat(result).doesNotContainKey("Salary");
    }
    @Test
    public void shouldPrintTotalExpensesByCategory() {
        when(transactionRepository.getUserTransactions(userId)).thenReturn(transactions);
        transactionService.getExpensesBySpecificCategory(userId,"Food", LocalDate.of(2025, 3, 1));
        String output = outContent.toString().trim();
        assertThat(output).contains("Сумма расходов по категории: Food| 50.0");
    }


}
