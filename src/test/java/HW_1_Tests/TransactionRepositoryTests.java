package HW_1_Tests;

import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.RepositoryInMap.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionRepositoryTests {
    @Spy
    private  Map<Long, List<Transaction>> transactions = new HashMap<>();
    private TransactionRepository transactionRepository;
    private Transaction transaction1;
    private Transaction transaction2;
    private Long userId;
    private Long transactionUUID;


    @BeforeEach
    void SetUp() throws NoSuchFieldException, IllegalAccessException {
        transactionRepository = new TransactionRepository();


        Field field = TransactionRepository.class.getDeclaredField("transactions");
        field.setAccessible(true);
        field.set(transactionRepository, transactions);


        userId = 1L;
        transactionUUID = 2L;
        transaction1 = new Transaction(transactionUUID, userId, TransactionType.INCOME, BigDecimal.valueOf(100),
                "Salary", LocalDate.now(), "Monthly salary");
        transaction2 = new Transaction(3L, userId, TransactionType.EXPENSE, BigDecimal.valueOf(50),
                "Food", LocalDate.now(), "Lunch");
    }

    @Test
    void testAddTransaction_NewTransactionForUser() {

        transactionRepository.addTransaction(transaction1);
        verify(transactions).computeIfAbsent(eq(userId), any());

    }

    @Test
    void testGetUserTransactions_UserHasTransactions() {
        transactionRepository.addTransaction(transaction1);
        List<Transaction> transactions = transactionRepository.getUserTransactions(userId);
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        assertEquals(transaction1, transactions.get(0));
    }

    @Test
    void testGetUserTransactions_NoTransactions() {
        List<Transaction> result = transactionRepository.getUserTransactions(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetUserTransactions_HaveTransactions() {
        transactionRepository.addTransaction(transaction1);
        transactionRepository.addTransaction(transaction2);
        List<Transaction> result = transactionRepository.getUserTransactions(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetUserExpenseTransactions() {
        //when(transactions.get(userId)).thenReturn(Arrays.asList(transaction1, transaction2));
        transactions.put(userId,Arrays.asList(transaction1, transaction2));
        List<Transaction> result = transactionRepository.getUserExpenseTransactions(userId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TransactionType.EXPENSE, result.get(0).getType());
    }

    @Test
    void testDeleteTransaction_TransactionExists() {
        List<Transaction> transactionsList = new ArrayList<>();
        transactionsList.add(transaction1);
        when(transactions.get(userId)).thenReturn(transactionsList);
        boolean result = transactionRepository.deleteTransaction(userId, transactionUUID);
        assertTrue(result);
        verify(transactions).get(userId);
        assertTrue(transactionsList.isEmpty());
    }

    @Test
    void testDeleteTransaction_TransactionNotFound() {
        Long userId = 1L;
        Long transactionId = 2L;
        when(transactions.get(userId)).thenReturn(null);
        boolean result = transactionRepository.deleteTransaction(userId, transactionId);
        assertFalse(result);
    }

    @Test
    void testUpgradeTransaction_Success() {
        when(transactions.get(userId)).thenReturn(new ArrayList<>(Collections.singletonList(transaction1)));
        boolean result = transactionRepository.upgradeTransaction(userId, transactionUUID, transaction2);
        assertTrue(result);
    }
    @Test
    void testUpgradeTransaction_TransactionNotFound() {
        when(transactions.get(userId)).thenReturn(new ArrayList<>());
        boolean result = transactionRepository.upgradeTransaction(userId, transactionUUID, transaction2);
        assertFalse(result);
    }


}
