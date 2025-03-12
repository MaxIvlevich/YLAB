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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    private TransactionRepositoryInterface transactionRepository;
    private TransactionService transactionService;
    private UUID userId;
    private UUID transaction1_Id;
    private UUID transaction2_Id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String category;
    private String description;
    private Transaction transaction1;
    private Transaction transaction2;


    @BeforeEach
    void setUp() {

        transactionRepository = Mockito.mock(TransactionRepositoryInterface.class);
        transactionService = new TransactionService(transactionRepository);

        userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        transaction1_Id = UUID.fromString("d9bff720-ccc7-491d-b4ec-fa71b21d2752");
        transaction2_Id = UUID.fromString("145d8128-7d65-4c06-873d-2739e619e0d0");
        transactionType = TransactionType.INCOME;
        amount = BigDecimal.valueOf(100);
        category = "Salary";
        description = "Monthly salary";

        transaction1 = new Transaction(transaction1_Id, userId, TransactionType.INCOME, BigDecimal.valueOf(100),
                "Salary", LocalDate.now(), "Monthly salary");
        transaction2 = new Transaction(transaction2_Id, userId, TransactionType.EXPENSE, BigDecimal.valueOf(50),
                "Food", LocalDate.now(), "Lunch");


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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        System.setOut(printStream);


        transactionService.deleteTransaction(userId, transaction1_Id);

        verify(transactionRepository, times(1)).deleteTransaction(userId, transaction1_Id);


    }

}
