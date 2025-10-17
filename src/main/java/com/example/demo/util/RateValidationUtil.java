package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.exception.DuplicateRateException;
import com.example.demo.repository.RateRepository;

import java.time.LocalDate;
import java.util.List;

public class RateValidationUtil {

    public static void checkDuplicate(Rate newRate, RateRepository repository) {
        List<Rate> duplicates = repository.findByBungalowIdAndBookDateToIsNullAndValueAndNights(
                        newRate.getBungalowId(),
                        newRate.getValue(),
                        newRate.getNights()
                ).stream()
                .filter(r -> {
                    LocalDate existingFrom = r.getStayDateFrom();
                    LocalDate existingTo = r.getStayDateTo() != null ? r.getStayDateTo() : LocalDate.MAX;
                    return !existingTo.isBefore(newRate.getStayDateFrom())
                            && !existingFrom.isAfter(newRate.getStayDateTo());
                })
                .toList();

        if (!duplicates.isEmpty()) {
            throw new DuplicateRateException("Duplicate rate exists for the given period!");
        }
    }
}

