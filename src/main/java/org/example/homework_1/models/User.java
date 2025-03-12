package org.example.homework_1.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.homework_1.models.enums.Roles;
import org.example.homework_1.models.enums.Status;

import java.util.UUID;
/**
 * Represents a user in the system.
 * This class is used to store the details of a user, including their name, email, password,
 * roles, and status. The user also has a unique identifier (UUID) generated when a user is created.
 *
 */
@Data
@AllArgsConstructor
public class User {
    private UUID userId;
    private String name;
    private String email;
    private String password;
    private Roles roles;
    private Status status;
    public User(String name, String email, String password,Roles roles,Status status) {
        this.userId = UUID.randomUUID();  // Генерация уникального ID
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles=roles;
        this.status = status;
    }


}



