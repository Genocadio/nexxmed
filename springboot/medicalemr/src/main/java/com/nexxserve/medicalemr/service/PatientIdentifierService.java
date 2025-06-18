package com.nexxserve.medicalemr.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import com.nexxserve.medicalemr.repository.PatientRepository;

@Service
public class PatientIdentifierService {

    private final PatientRepository patientRepository;
    private static final DateTimeFormatter YEAR_FORMAT = DateTimeFormatter.ofPattern("yy");
    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM");

    // Cache for the current month's counter
    private String currentYearMonth = "";
    private AtomicInteger monthlyCounter = new AtomicInteger(0);

    public PatientIdentifierService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    public String generatePatientIdentifier(String firstName, String lastName) {
        LocalDateTime now = LocalDateTime.now();
        String yearCode = now.format(YEAR_FORMAT);
        String monthCode = now.format(MONTH_FORMAT);
        String yearMonth = yearCode + monthCode;

        // Get initials from name
        String initials = getInitials(firstName, lastName);

        // Reset counter if month changed
        if (!yearMonth.equals(currentYearMonth)) {
            synchronized (this) {
                if (!yearMonth.equals(currentYearMonth)) {
                    currentYearMonth = yearMonth;
                    int maxCounter = patientRepository.findMaxCounterForYearMonth(yearMonth);
                    monthlyCounter.set(maxCounter + 1);
                }
            }
        }

        // Get next counter value
        int counterValue = monthlyCounter.getAndIncrement();
        String counterStr = String.format("%04d", counterValue);

        // Format: INITIALS-YYMM-COUNTER (e.g., JD-2305-0001)
        return initials + "-" + yearMonth + "-" + counterStr;
    }

    private String getInitials(String firstName, String lastName) {
        StringBuilder initials = new StringBuilder();

        if (firstName != null && !firstName.isEmpty()) {
            initials.append(firstName.substring(0, 1).toUpperCase());
        } else {
            initials.append("X");
        }

        if (lastName != null && !lastName.isEmpty()) {
            initials.append(lastName.substring(0, 1).toUpperCase());
        } else {
            initials.append("X");
        }

        return initials.toString();
    }
}