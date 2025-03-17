package org.example.homework_1.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConfig {
    private static Connection connection;
    public static Connection getConnection(ConfigReader configReader) throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (DatabaseConfig.class) {
                if (connection == null || connection.isClosed()) {
                    try {
                        String url = configReader.getDbUrl();
                        String username = configReader.getDbUsername();
                        String password = configReader.getDbPassword();
                        Class.forName("org.postgresql.Driver");
                        connection = DriverManager.getConnection(url, username, password);
                        System.out.println("✅ Открыто новое соединение: " + connection);
                    } catch (SQLException e) {
                        System.err.println("❌ Ошибка при подключении к БД: " + e.getMessage());
                        throw e;
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        System.out.println("🔍 Возвращаем соединение: " + connection);
        return connection;
    }
    public static Connection getLiquibaseConnection(ConfigReader configReader) throws SQLException {
        String url = configReader.getLiquibaseUrl();
        String username = configReader.getLiquibaseUsername();
        String password = configReader.getLiquibasePassword();
        try {
            Connection liquibaseConnection = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Открыто новое соединение для Liquibase: " + liquibaseConnection);
            return liquibaseConnection;
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при подключении к БД для Liquibase: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Соединение с БД закрыто");
        }
    }


}
