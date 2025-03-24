package org.example.homework_1;

import org.example.homework_1.app.App;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    static App app;
    static {
        try {
            app = new App();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        app.startApp();
    }
}






