package org.example.homework_1.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConfig {
    private static Connection connection;
    public static Connection getConnection(ConfigReader configReader) throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = configReader.getDbUrl();
            String username = configReader.getDbUsername();
            String password = configReader.getDbPassword();
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }


}
