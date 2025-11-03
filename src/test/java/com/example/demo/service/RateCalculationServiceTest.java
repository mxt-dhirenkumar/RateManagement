package com.example.demo.service;

import com.example.demo.entity.Rate;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

class RateCalculationServiceTest {

    @Test
    void testCalculateTotalAmount() {
        Rate rate1 = new Rate();
        rate1.setStayDateFrom(LocalDate.of(2024, 7, 10));
        rate1.setStayDateTo(LocalDate.of(2024, 7, 12));
        rate1.setValue(100L);

        Rate rate2 = new Rate();
        rate2.setStayDateFrom(LocalDate.of(2024, 7, 12));
        rate2.setStayDateTo(LocalDate.of(2024, 7, 14));
        rate2.setValue(150L);

        long result = RateCalculationService.calculateTotalAmount(
                Arrays.asList(rate1, rate2),
                LocalDate.of(2024, 7, 10),
                LocalDate.of(2024, 7, 13)
        );

        // rate1: 2 days (10,11), rate2: 1 day (12)
        // total = 2*100 + 1*150 = 350
        assertEquals(350L, result);
    }
}

