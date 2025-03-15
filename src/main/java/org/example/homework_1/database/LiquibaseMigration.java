package org.example.homework_1.database;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.core.PostgresDatabase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

public class LiquibaseMigration {
    public static void runMigration(ConfigReader configReader) throws Exception {
        String changeLogFile = "db.changelog/changelog-master.xml";
        String url = configReader.getLiquibaseUrl();
        String username = configReader.getLiquibaseUsername();
        String password = configReader.getLiquibasePassword();

        try (Connection connection = DatabaseConfig.getConnection(configReader)) {
            Database database = new PostgresDatabase();
            database.setConnection(new JdbcConnection(connection));

            try (Liquibase liquibase = new Liquibase(changeLogFile,  new ClassLoaderResourceAccessor(), database)) {

                liquibase.update("");
            }
        }
    }
}
