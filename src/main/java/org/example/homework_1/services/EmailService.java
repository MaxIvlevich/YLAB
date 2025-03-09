package org.example.homework_1.services;


public class EmailService {


    public void sendEmail(String toEmail, String subject, String body) {
        // Симуляция отправки email
        System.out.println("📧 Email отправлен на: " + toEmail);
        System.out.println("Тема: " + subject);
        System.out.println("Сообщение: " + body);
    }


}
