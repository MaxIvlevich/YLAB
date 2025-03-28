package org.example.homework_1.servlets;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.annotation.WebListener;
import org.example.homework_1.database.ConfigReader;
import org.example.homework_1.database.DatabaseConfig;
import org.example.homework_1.mappers.GoalMapper;
import org.example.homework_1.mappers.TransactionMapper;
import org.example.homework_1.mappers.UserMapper;
import org.example.homework_1.repository.JDBCRepositoryes.TransactionRepositoryJDBC;
import org.example.homework_1.repository.JDBCRepositoryes.UserRepositoryJDBC;
import org.example.homework_1.repository.JDBCRepositoryes.WalletRepositoryJDBC;
import org.example.homework_1.repository.RepositiryInterfaces.TransactionRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.UserRepositoryInterface;
import org.example.homework_1.repository.RepositiryInterfaces.WalletRepositoryInterface;
import org.example.homework_1.services.EmailService;
import org.example.homework_1.services.Interfaces.EmailServiceInterface;
import org.example.homework_1.services.Interfaces.TransactionServiceInterface;
import org.example.homework_1.services.Interfaces.UserServiceInterface;
import org.example.homework_1.services.Interfaces.WalletServiceInterface;
import org.example.homework_1.services.TransactionService;
import org.example.homework_1.services.UserServiceImpl;
import org.example.homework_1.services.WalletServiceImpl;
import java.sql.Connection;
import java.sql.SQLException;

@WebListener
public class DependencyInjectionListener implements ServletContextListener {
    private Connection dbConnection;
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Application context initialization started...");
        ServletContext context = sce.getServletContext();

        try {
            System.out.println("Initializing database connection...");
            ConfigReader configReader = new ConfigReader("config.properties");
            dbConnection = DatabaseConfig.getConnection(configReader);
            System.out.println("Database connection established.");


            System.out.println("Creating ObjectMapper...");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            System.out.println("ObjectMapper created.");
            System.out.println("Creating Repositories...");
            UserRepositoryInterface userRepository = new UserRepositoryJDBC(dbConnection);
            WalletRepositoryInterface walletRepository = new WalletRepositoryJDBC(dbConnection);
            TransactionRepositoryInterface transactionRepository = new TransactionRepositoryJDBC(dbConnection);
            EmailServiceInterface emailService = new EmailService();

            System.out.println("Repositories created.");
            System.out.println("Creating Services...");

            UserServiceInterface userService = new UserServiceImpl(userRepository);
            WalletServiceInterface walletService = new WalletServiceImpl(walletRepository, transactionRepository, emailService, userService);
            TransactionServiceInterface transactionService = new TransactionService(transactionRepository);
            System.out.println("Services created.");

            System.out.println("Creating and Registering Servlets...");

            AuthServlet authServlet = new AuthServlet(userService, walletService, objectMapper);
            ServletRegistration.Dynamic authReg = context.addServlet("AuthServlet", authServlet);
            authReg.addMapping("/api/auth/*");
            System.out.println("AuthServlet registered at /api/auth/*");

            UserServlet userServlet = new UserServlet(objectMapper, userService, UserMapper.INSTANCE);
            ServletRegistration.Dynamic userReg = context.addServlet("UserServlet", userServlet);
            userReg.addMapping("/api/users/*");
            System.out.println("UserServlet registered at /api/users/*");

            AddTransactionServlet addTransactionServlet = new AddTransactionServlet(transactionService, objectMapper,
                    TransactionMapper.INSTANCE, userService);
            ServletRegistration.Dynamic addTransactionReg = context.addServlet("addTransactionServlet", addTransactionServlet);
            addTransactionReg.addMapping("/api/transactions/add");
            System.out.println("TransactionServlet registered at /api/transactions/add");

            DeleteTransactionServlet deleteTransactionServlet = new DeleteTransactionServlet(transactionService, userService);
            ServletRegistration.Dynamic deleteTransaction = context.addServlet("deleteTransactionServlet", deleteTransactionServlet);
            deleteTransaction.addMapping("/api/transactions/delete");
            System.out.println("TransactionServlet registered at /api/transactions/delete");

            ShowTransactionsServlet showTransactionsServlet = new ShowTransactionsServlet(transactionService, objectMapper,
                    TransactionMapper.INSTANCE, userService);
            ServletRegistration.Dynamic showTransactions = context.addServlet("showTransactionsServlet", showTransactionsServlet);
            showTransactions.addMapping("/api/transactions");
            System.out.println("TransactionServlet registered at /api/transactions");

            UpdateTransactionServlet updateTransactionServlet = new UpdateTransactionServlet(transactionService, userService);
            ServletRegistration.Dynamic updateTransaction = context.addServlet("updateTransactionServlet", updateTransactionServlet);
            updateTransaction.addMapping("/api/transactions/update");
            System.out.println("TransactionServlet registered at /api/transactions/update");

            WalletServlet walletServlet = new WalletServlet(objectMapper, userService, walletService);
            ServletRegistration.Dynamic walletReg = context.addServlet("WalletServlet", walletServlet);
            walletReg.addMapping("/api/wallet/*"); // Или какой там путь был
            System.out.println("WalletServlet registered at /api/wallet/*");

            GoalServlet goalServlet = new GoalServlet(walletService, objectMapper, GoalMapper.INSTANCE, userService);
            ServletRegistration.Dynamic goalReg = context.addServlet("GoalServlet", goalServlet);
            goalReg.addMapping("/api/goals/*");
            System.out.println("GoalServlet registered at /api/goals/*");
            System.out.println("All servlets registered programmatically.");

        } catch (Exception e) {
            System.err.println("FATAL: Application context initialization failed!");
            e.printStackTrace();
            if (dbConnection != null) {
                try {
                    dbConnection.close();
                } catch (SQLException ex) { /* ignore */ }
            }
            throw new RuntimeException("Failed to initialize application context", e);
        }
        System.out.println("Application context initialization finished successfully.");
    }
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application context destruction started...");
        if (dbConnection != null) {
            try {
                System.out.println("Closing database connection...");
                dbConnection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace(); // Логировать!
            }
        }
        System.out.println("Application context destruction finished.");
    }
}
