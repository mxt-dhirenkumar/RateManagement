package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.exception.DuplicateRateException;
import com.example.demo.repository.RateRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Checks if a duplicate Rate exists in the repository for the given parameters.
 * <p>
 * It queries the repository for rates with the same bungalow ID, value, nights,
 * and overlapping stay dates where the booking date is null.
 * If a duplicate is found, a DuplicateRateException is thrown.
 * </p>
 */

public class RateValidationUtil {

    public static void checkDuplicate(Rate newRate, RateRepository repository) {
        List<Rate> duplicates =
                repository.findByBungalowIdAndValueAndNightsAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(
                        newRate.getBungalowId(),
                        newRate.getValue(),
                        newRate.getNights(),
                        newRate.getStayDateFrom(),
                        newRate.getStayDateTo()
                );

        if (!duplicates.isEmpty()) {
            throw new DuplicateRateException("Duplicate rate exists for the given period!");
        }
    }

}

