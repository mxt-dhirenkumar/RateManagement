package com.example.demo.service;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateCalculationService {

    private final RateRepository rateRepository;

    public long calculateTotalPrice(Long bungalowId, LocalDate stayFrom, LocalDate stayTo) {
        if (stayTo.isBefore(stayFrom)) {
            throw new IllegalArgumentException("Departure date cannot be before arrival date.");
        }

        // 1️⃣ Fetch overlapping active rates
        List<Rate> rates = rateRepository
                .findByBungalowIdAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(
                        bungalowId, stayTo, stayFrom);

        long total = 0;

        // 2️⃣ Calculate total per rate
        for (Rate rate : rates) {
            LocalDate overlapStart = stayFrom.isAfter(rate.getStayDateFrom()) ? stayFrom : rate.getStayDateFrom();
            LocalDate overlapEnd = stayTo.isBefore(rate.getStayDateTo()) ? stayTo : rate.getStayDateTo();

            long days = ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;
            if (days > 0) {
                total += days * rate.getValue();
            }
        }

        return total;
    }
}

