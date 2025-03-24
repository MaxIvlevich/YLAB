package org.example.homework_1.controllers;

import java.time.LocalDate;
import java.util.UUID;

public interface InformationServiceInterface {
    void generateReport(Long userId, LocalDate fromDate);

}
