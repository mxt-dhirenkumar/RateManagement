package com.example.demo.service;

import com.example.demo.entity.Rate;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RateCalculationServiceTest {

    // ✅ Basic overlapping scenario
    @Test
    void testCalculateTotalAmount_BasicOverlap() {
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

        // rate1: 2 nights (10–11), rate2: 1 night (12)
        assertEquals(350L, result);
    }

    // ✅ Case: Single rate fully covers the requested range
    @Test
    void testCalculateTotalAmount_SingleRateFullCoverage() {
        Rate rate = new Rate();
        rate.setStayDateFrom(LocalDate.of(2024, 7, 1));
        rate.setStayDateTo(LocalDate.of(2024, 7, 31));
        rate.setValue(200L);

        long result = RateCalculationService.calculateTotalAmount(
                List.of(rate),
                LocalDate.of(2024, 7, 10),
                LocalDate.of(2024, 7, 15)
        );

        // 5 nights (10–14) × 200 = 1000
        assertEquals(1000L, result);
    }

    // ✅ Case: Range partially overlaps start of rate period
    @Test
    void testCalculateTotalAmount_PartialOverlapAtStart() {
        Rate rate = new Rate();
        rate.setStayDateFrom(LocalDate.of(2024, 7, 5));
        rate.setStayDateTo(LocalDate.of(2024, 7, 10));
        rate.setValue(300L);

        long result = RateCalculationService.calculateTotalAmount(
                List.of(rate),
                LocalDate.of(2024, 7, 8),
                LocalDate.of(2024, 7, 12)
        );

        // Overlap = 8,9 → 2 nights × 300 = 600
        assertEquals(600L, result);
    }

    // ✅ Case: Range partially overlaps end of rate period
    @Test
    void testCalculateTotalAmount_PartialOverlapAtEnd() {
        Rate rate = new Rate();
        rate.setStayDateFrom(LocalDate.of(2024, 7, 10));
        rate.setStayDateTo(LocalDate.of(2024, 7, 20));
        rate.setValue(500L);

        long result = RateCalculationService.calculateTotalAmount(
                List.of(rate),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 12)
        );

        // Overlap = 10,11 → 2 nights × 500 = 1000
        assertEquals(1000L, result);
    }

    // ✅ Case: Multiple non-contiguous rates in between
    @Test
    void testCalculateTotalAmount_NonContiguousRates() {
        Rate rate1 = new Rate();
        rate1.setStayDateFrom(LocalDate.of(2024, 7, 1));
        rate1.setStayDateTo(LocalDate.of(2024, 7, 5));
        rate1.setValue(100L);

        Rate rate2 = new Rate();
        rate2.setStayDateFrom(LocalDate.of(2024, 7, 10));
        rate2.setStayDateTo(LocalDate.of(2024, 7, 12));
        rate2.setValue(200L);

        long result = RateCalculationService.calculateTotalAmount(
                Arrays.asList(rate1, rate2),
                LocalDate.of(2024, 7, 2),
                LocalDate.of(2024, 7, 11)
        );

        // rate1: 3 nights (2–4) × 100 = 300
        // rate2: 1 night (10) × 200 = 200
        assertEquals(500L, result);
    }

    // ✅ Case: No matching rate periods
    @Test
    void testCalculateTotalAmount_NoOverlap() {
        Rate rate = new Rate();
        rate.setStayDateFrom(LocalDate.of(2024, 6, 1));
        rate.setStayDateTo(LocalDate.of(2024, 6, 10));
        rate.setValue(100L);

        long result = RateCalculationService.calculateTotalAmount(
                List.of(rate),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 10)
        );

        // No overlap
        assertEquals(0L, result);
    }

    // ✅ Case: Empty rate list
    @Test
    void testCalculateTotalAmount_EmptyList() {
        long result = RateCalculationService.calculateTotalAmount(
                Collections.emptyList(),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 10)
        );

        assertEquals(0L, result);
    }

    // ✅ Case: Overlapping rates with same dates but different values — should sum both
    @Test
    void testCalculateTotalAmount_OverlappingRatesWithDifferentValues() {
        Rate rate1 = new Rate();
        rate1.setStayDateFrom(LocalDate.of(2024, 7, 1));
        rate1.setStayDateTo(LocalDate.of(2024, 7, 5));
        rate1.setValue(100L);

        Rate rate2 = new Rate();
        rate2.setStayDateFrom(LocalDate.of(2024, 7, 3));
        rate2.setStayDateTo(LocalDate.of(2024, 7, 6));
        rate2.setValue(200L);

        long result = RateCalculationService.calculateTotalAmount(
                Arrays.asList(rate1, rate2),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 6)
        );

        // Days 1–2 = 100×2 = 200, Days 3–4 = (100+200)×2 = 600, Day 5 = 200×1 = 200 → Total = 1000
        assertEquals(1000L, result);
    }

    // ✅ Case: Stay range exactly matches rate boundaries
    @Test
    void testCalculateTotalAmount_ExactMatchBoundaries() {
        Rate rate = new Rate();
        rate.setStayDateFrom(LocalDate.of(2024, 7, 1));
        rate.setStayDateTo(LocalDate.of(2024, 7, 5));
        rate.setValue(250L);

        long result = RateCalculationService.calculateTotalAmount(
                List.of(rate),
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 5)
        );

        // Nights = 4 × 250 = 1000
        assertEquals(1000L, result);
    }
}
