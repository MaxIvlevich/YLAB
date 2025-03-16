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
        try (Connection liquibaseConnection = DatabaseConfig.getLiquibaseConnection(configReader)) {
            Database database = new PostgresDatabase();
            database.setConnection(new JdbcConnection(liquibaseConnection));

            try (Liquibase liquibase = new Liquibase(changeLogFile,  new ClassLoaderResourceAccessor(), database)) {

                liquibase.update("");
            }
        }
    }
}
