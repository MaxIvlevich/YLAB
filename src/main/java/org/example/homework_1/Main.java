package org.example.homework_1;

import org.example.homework_1.services.TestService;

public class Main {
    public static void main(String[] args) {

        TestService service = new TestService();
        service.testMethod(); // Должно залогироваться через аспект

    }

}







