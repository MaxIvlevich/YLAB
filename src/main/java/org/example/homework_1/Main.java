package org.example.homework_1;

import org.example.homework_1.app.App;

import java.io.IOException;

public class Main {
    static App app;

    static {
        try {
            app = new App();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        app.startApp();
    }
}






