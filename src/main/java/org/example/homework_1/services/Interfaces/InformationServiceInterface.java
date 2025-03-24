package org.example.homework_1.services.Interfaces;

import java.time.LocalDate;
import java.util.UUID;

public interface InformationServiceInterface {
    void generateReport(Long userId, LocalDate fromDate);

}
