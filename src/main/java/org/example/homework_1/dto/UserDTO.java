package org.example.homework_1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
public record UserDTO(
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {}


