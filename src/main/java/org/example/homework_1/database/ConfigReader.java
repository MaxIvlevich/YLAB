package org.example.homework_1.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private final Properties properties;

    public ConfigReader(String configFile) throws IOException {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
        }
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
