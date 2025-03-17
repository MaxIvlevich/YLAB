package org.example.homework_1.database;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;

public class LiquibaseHelper {
    public static void runLiquibase(Connection connection) {
        try {
            Database database = new liquibase.database.core.PostgresDatabase();
            database.setConnection(new JdbcConnection(connection));

            Liquibase liquibase = new Liquibase("db.changelog/changelog-master-test.xml",
                    new ClassLoaderResourceAccessor(), database);

            liquibase.update("");
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при запуске Liquibase", e);
        }
    }
}
