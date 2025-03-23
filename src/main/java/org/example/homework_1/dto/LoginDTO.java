package org.example.homework_1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;


public record LoginDTO(
        @JsonProperty("email") String email,
        @JsonProperty("password") String password
) {}
