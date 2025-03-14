package HW_1_Tests;

import org.example.homework_1.models.Wallet;
import org.example.homework_1.repository.RepositoryInMap.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WalletRepositoryTests {
    private  WalletRepository walletRepository;
    @Spy
    private  Map<UUID, Wallet> userWallets = new HashMap<>();

    private UUID userId;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        walletRepository = new WalletRepository();

        Field field = WalletRepository.class.getDeclaredField("userWallets");
        field.setAccessible(true);
        field.set(walletRepository, userWallets);

        userId = UUID.randomUUID();

    }

    @Test
    void testInitializeWallet_NewWalletForUser() {
        walletRepository.initializeWallet(userId);
        Wallet wallet = walletRepository.getUserWallet(userId);
        assertNotNull(wallet, "Кошелек для пользователя должен быть создан.");
        assertEquals(userId, wallet.getUserId(), "ID пользователя в кошельке должен совпадать с ID, переданным в метод.");
    }
    @Test
    void testInitializeWallet_ExistingWallet() {
        walletRepository.initializeWallet(userId);
        walletRepository.initializeWallet(userId);
        Wallet wallet = walletRepository.getUserWallet(userId);
        assertNotNull(wallet, "Кошелек для пользователя должен быть создан.");
        assertEquals(userId, wallet.getUserId(), "ID пользователя в кошельке должен совпадать с ID, переданным в метод.");
    }
    @Test
    void testSetBudget_Success() {
        walletRepository.initializeWallet(userId);
        double expectedBudget = 1000.00;
        walletRepository.setBudget(userId, expectedBudget);
        Wallet wallet = walletRepository.getUserWallet(userId);
        assertNotNull(wallet, "Кошелек для пользователя должен быть создан.");
        assertEquals(expectedBudget, wallet.getMonthlyBudget(), "Бюджет должен быть установлен корректно.");
    }

    @Test
    void testSetBudget_UserNotFound() {
        double expectedBudget = 1000.00;
        walletRepository.setBudget(userId, expectedBudget);
        Wallet wallet = walletRepository.getUserWallet(userId);
        assertNull(wallet, "Кошелек не должен быть создан для несуществующего пользователя.");
    }
    @Test
    void testGetBudget_UserExists() {
        walletRepository.initializeWallet(userId);
        double expectedBudget = 1000.00;
        walletRepository.setBudget(userId, expectedBudget);
        double actualBudget = walletRepository.getBudget(userId);
        assertEquals(expectedBudget, actualBudget, "Бюджет пользователя должен быть равен 1000.00");
    }

    @Test
    void testGetBudget_UserNotExists() {
        double budget = walletRepository.getBudget(userId);
        assertEquals(0.0, budget, "Если кошелек пользователя не существует, должен возвращаться бюджет 0.0");
    }
    @Test
    void testAddGoal_UserExists() {
        walletRepository.initializeWallet(userId);
        String goalName = "Покупка нового ноутбука";
        BigDecimal targetAmount = BigDecimal.valueOf(50000);
        walletRepository.addGoal(userId, goalName, targetAmount);
        Wallet wallet = userWallets.get(userId);
        assertTrue(wallet.getSavingsGoals().containsKey(goalName));
        assertEquals(targetAmount, wallet.getSavingsGoals().get(goalName));
    }
    @Test
    void testGetUserGoals_UserExists() {
        walletRepository.initializeWallet(userId);
        String goalName = "Покупка нового дома";
        BigDecimal targetAmount = BigDecimal.valueOf(200000);
        walletRepository.addGoal(userId, goalName, targetAmount);
        Map<String, BigDecimal> goals = walletRepository.getUserGoals(userId);
        assertNotNull(goals);
        assertFalse(goals.isEmpty());
        assertTrue(goals.containsKey(goalName));
        assertEquals(targetAmount, goals.get(goalName));
    }

    @Test
    void testIsGoalAchieved_GoalExists() {
        walletRepository.initializeWallet(userId);
        String goalName = "Покупка автомобиля";
        BigDecimal targetAmount = BigDecimal.valueOf(50000);
        walletRepository.addGoal(userId, goalName, targetAmount);
        BigDecimal balance = BigDecimal.valueOf(60000);
        assertTrue(walletRepository.isGoalAchieved(userId, goalName, balance));
        balance = BigDecimal.valueOf(40000);
        assertFalse(walletRepository.isGoalAchieved(userId, goalName, balance));
    }



}
