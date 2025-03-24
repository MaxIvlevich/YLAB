package org.example.homework_1.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {

    private final Properties properties = new Properties();

    public ConfigReader(String configFile) throws IOException {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                throw new IOException("Файл " + configFile + " не найден в classpath!");
            }
            properties.load(input);
        }

    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password");
    }

    public String getDbSchema() {
        return properties.getProperty("db.schema");
    }

    public String getLiquibaseChangeLogFile() {
        return properties.getProperty("liquibase.changeLogFile");
    }

    public String getLiquibaseDriver() {
        return properties.getProperty("liquibase.driver");
    }

    public String getLiquibaseUrl() {
        return properties.getProperty("liquibase.url");
    }

    public String getLiquibaseUsername() {
        return properties.getProperty("liquibase.username");
    }

    public String getLiquibasePassword() {
        return properties.getProperty("liquibase.password");
    }
}
