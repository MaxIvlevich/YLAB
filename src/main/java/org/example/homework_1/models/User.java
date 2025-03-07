package org.example.homework_1.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class User {
    private UUID userId;
    private String name;
    private String email;
    private String password;
    public User(String name, String email, String password) {
        this.userId = UUID.randomUUID();  // Генерация уникального ID
        this.name = name;
        this.email = email;
        this.password = password;
    }


}



