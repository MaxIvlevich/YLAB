package org.example.homework_1.services;


import org.example.homework_1.services.Interfaces.EmailServiceInterface;

public class EmailService implements EmailServiceInterface {
    @Override
    public void sendEmail(String toEmail, String subject, String body) {

        System.out.println("Email отправлен на: " + toEmail);
        System.out.println("Тема: " + subject);
        System.out.println("Сообщение: " + body);
    }
}
