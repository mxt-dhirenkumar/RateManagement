package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.exception.DuplicateRateException;
import com.example.demo.repository.RateRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Utility class for validating {@link Rate} entities to prevent duplicates.
 * <p>
 * The {@code checkDuplicate} method checks if a new {@code Rate} overlaps exactly with any existing
 * active rate (same bungalow, value, and nights) in the repository. It retrieves all active rates
 * matching the new rate's attributes and determines if the new rate's date range is fully contained
 * within any existing rate's date range. If such a duplicate is found, a {@link DuplicateRateException}
 * is thrown to prevent insertion of redundant rates.
 * <p>
 * This ensures data integrity by avoiding duplicate rate entries for the same period and attributes.
 */

public class RateValidationUtil {

    public static void checkDuplicate(Rate newRate, RateRepository repository) {
        List<Rate> activeRates = repository.findByBungalowIdAndBookDateToIsNullAndValueAndNights(
                newRate.getBungalowId(),
                newRate.getValue(),
                newRate.getNights()
        );

        boolean hasExactDuplicate = activeRates.stream()
                .anyMatch(existing -> {
                    LocalDate existingFrom = existing.getStayDateFrom();
                    LocalDate existingTo = existing.getStayDateTo() != null ? existing.getStayDateTo() : LocalDate.MAX;

                    // Treat as duplicate only if new range is fully inside the existing range
                    boolean fullyInside =
                            !newRate.getStayDateFrom().isBefore(existingFrom) &&
                                    !newRate.getStayDateTo().isAfter(existingTo);

                    return fullyInside;
                });

        if (hasExactDuplicate) {
            throw new DuplicateRateException("Duplicate rate exists for the given period!");
        }
    }
}

