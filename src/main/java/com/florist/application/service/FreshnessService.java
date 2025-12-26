package com.florist.application.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Service to calculate flower freshness based on arrival date and shelf life.
 */
public class FreshnessService {

    public enum FreshnessStatus {
        FRESH("Fresh"),
        ACCEPTABLE("Acceptable"),
        EXPIRED("Expired");

        private final String label;

        FreshnessStatus(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public static int calculateFreshnessPercentage(LocalDate arrivalDate, int maxFreshDays) {
        if (arrivalDate == null || maxFreshDays <= 0) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        if (arrivalDate.isAfter(today)) {
            return 100;
        }

        long daysPassed = ChronoUnit.DAYS.between(arrivalDate, today);
        if (daysPassed >= maxFreshDays) {
            return 0;
        }

        double percentage = 100.0 * (1.0 - ((double) daysPassed / maxFreshDays));
        return (int) Math.max(0, Math.min(100, Math.round(percentage)));
    }

    public static FreshnessStatus getFreshnessStatus(int percentage) {
        if (percentage >= 70) {
            return FreshnessStatus.FRESH;
        } else if (percentage >= 40) {
            return FreshnessStatus.ACCEPTABLE;
        } else {
            return FreshnessStatus.EXPIRED;
        }
    }

    public static String getFreshnessLabel(LocalDate arrivalDate, int maxFreshDays) {
        int percentage = calculateFreshnessPercentage(arrivalDate, maxFreshDays);
        FreshnessStatus status = getFreshnessStatus(percentage);
        return String.format("%s (%d%%)", status.getLabel(), percentage);
    }
}
