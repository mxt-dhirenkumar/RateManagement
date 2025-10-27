package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.exception.DuplicateRateException;
import com.example.demo.repository.RateRepository;

import java.time.LocalDate;
import java.util.List;

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

