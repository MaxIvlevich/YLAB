package org.example.homework_1.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConfig {
    private static Connection connection;
    public static Connection getConnection(ConfigReader configReader) throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                String url = configReader.getDbUrl();
                String username = configReader.getDbUsername();
                String password = configReader.getDbPassword();
                connection = DriverManager.getConnection(url, username, password);
            }catch (SQLException e) {
                    System.err.println("Error while connecting to the database: " + e.getMessage());
                    throw e;
                }
        }
        return connection;
    }
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Соединение с БД закрыто");
        }
    }


}
