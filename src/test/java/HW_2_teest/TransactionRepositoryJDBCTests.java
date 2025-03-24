package HW_2_teest;

import org.example.homework_1.database.LiquibaseHelper;
import org.example.homework_1.models.Transaction;
import org.example.homework_1.models.User;
import org.example.homework_1.models.enums.TransactionType;
import org.example.homework_1.repository.JDBCRepositoryes.TransactionRepositoryJDBC;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.example.homework_1.models.enums.Roles.ADMIN;
import static org.example.homework_1.models.enums.Status.ACTIVE;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionRepositoryJDBCTests {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    private Connection connection;
    private TransactionRepositoryJDBC transactionRepository;
    private UserRepositoryJDBC userRepository;
    private  Transaction transaction;
    private Long userId;

    @BeforeAll
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        LiquibaseHelper.runLiquibase(connection);
        userRepository = new UserRepositoryJDBC(connection);
        transactionRepository = new TransactionRepositoryJDBC(connection);
        User user = new User("ylab app", "ylab@example.com", "hashed_password", ADMIN, ACTIVE);

        userRepository.addUser(user);
        userId = userRepository.getUserByEmail("ylab@example.com").getUserId();


    }
    @BeforeEach
    void cleanUp() throws SQLException {
        String sql = "DELETE FROM app.transactions";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }
    @AfterAll
    void tearDown() throws SQLException {
        connection.close();
    }
    @Test
    void addTransactionTest(){

        transaction = new Transaction(userId,TransactionType.INCOME, BigDecimal.valueOf(100),
                "Salary", LocalDate.now(), "Monthly salary");
        transactionRepository.addTransaction(transaction);
        List<Transaction> transactions = transactionRepository.getUserTransactions(userId);
        assertNotNull(transactions);

        boolean found = transactions.stream().anyMatch(t ->
                t.getAmount().compareTo(BigDecimal.valueOf(100)) == 0 &&
                        t.getType() == TransactionType.INCOME &&
                        "Salary".equals(t.getCategory()) &&
                        "Monthly salary".equals(t.getDescription()) &&
                        t.getUserUUID().equals(userId)

        );
        assertTrue(found, "Добавленная транзакция не найдена среди полученных");

    }
    @Test
    void getUserExpenseTransactionsTest() {
        Transaction expense = new Transaction(
                userId,
                TransactionType.EXPENSE,
                BigDecimal.valueOf(100),
                "Shopping",
                LocalDate.now(),
                "Bought groceries"
        );
        transactionRepository.addTransaction(expense);
        List<Transaction> expenses = transactionRepository.getUserExpenseTransactions(userId);
        assertNotNull(expenses, "Список расходов не должен быть null");
        assertFalse(expenses.isEmpty(), "Список расходов не должен быть пустым");
        boolean found = expenses.stream().anyMatch(t -> t.getCategory().equals("Shopping"));
        assertTrue(found, "Добавленная расходная транзакция не найдена");
    }
    @Test
    void deleteTransactionTest() {
        transaction = new Transaction(userId,TransactionType.INCOME, BigDecimal.valueOf(100),
                "Salary", LocalDate.now(), "Monthly salary");
        transactionRepository.addTransaction(transaction);
        Long transactionId = transactionRepository.getUserTransactions(userId).get(0).getTransactionUUID();
        boolean deleted = transactionRepository.deleteTransaction(userId, transactionId);
        assertTrue(deleted, "Транзакция должна быть удалена");

        List<Transaction> transactions = transactionRepository.getUserTransactions(userId);
        boolean stillExists = transactions.stream().anyMatch(t -> t.getTransactionUUID().equals(transactionId));
        assertFalse(stillExists, "Удаленная транзакция не должна существовать");
    }

    @Test
    void upgradeTransactionTest() {
        transaction = new Transaction(userId,TransactionType.INCOME, BigDecimal.valueOf(100),
                "Salary", LocalDate.now(), "Monthly salary");
        transactionRepository.addTransaction(transaction);
        Transaction updatedTransaction = new Transaction(
                userId,
                TransactionType.INCOME,
                BigDecimal.valueOf(1000),
                "Bonus",
                LocalDate.now(),
                "Updated description"
        );
        Long transactionId = transactionRepository.getUserTransactions(userId).get(0).getTransactionUUID();

        boolean updated = transactionRepository.upgradeTransaction(userId, transactionId, updatedTransaction);
        assertTrue(updated, "Транзакция должна быть обновлена");
        List<Transaction> transactions = transactionRepository.getUserTransactions(userId);
        boolean found = transactions.stream().anyMatch(t ->
                t.getTransactionUUID().equals(transactionId) &&
                        t.getAmount().compareTo(BigDecimal.valueOf(1000)) == 0 &&
                        "Bonus".equals(t.getCategory()) &&
                        "Updated description".equals(t.getDescription())
        );

        assertTrue(found, "Обновленная транзакция не найдена");
    }
}
