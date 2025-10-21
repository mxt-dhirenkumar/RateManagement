package com.example.demo.service;

import com.example.demo.entity.Rate;
import com.example.demo.repository.InMemoryRateRepository;
import com.example.demo.util.RateMergeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MergeWithNextTest {

    @Test
    void mergeWithNext_shouldExtendNewRateAndCloseNext() {
        // Arrange
        List<Rate> db = new ArrayList<>();

        Rate april = new Rate();
        april.setBungalowId(100L);
        april.setStayDateFrom(LocalDate.of(2024, 4, 1));
        april.setStayDateTo(LocalDate.of(2024, 4, 30));
        april.setValue(3000L);
        april.setNights(1);
        april.setBookDateFrom(LocalDateTime.now());
        april.setBookDateTo(null);
        db.add(april);

        Rate march = new Rate();
        march.setBungalowId(100L);
        march.setStayDateFrom(LocalDate.of(2024, 3, 1));
        march.setStayDateTo(LocalDate.of(2024, 3, 31));
        march.setValue(3000L);
        march.setNights(1);

        InMemoryRateRepository repo = new InMemoryRateRepository(db);

        // Act: merge new March rate with neighbors
        Rate result = RateMergeUtil.mergeNeighbors(march, repo);

        // Assert
        // April rate should be closed
        Assertions.assertNotNull(april.getBookDateTo());

        // March rate should extend to cover April
        Assertions.assertEquals(LocalDate.of(2024, 3, 1), result.getStayDateFrom());
        Assertions.assertEquals(LocalDate.of(2024, 4, 30), result.getStayDateTo());

        // March rate is active
        Assertions.assertNull(result.getBookDateTo());

        // Database should have 2 entries
        Assertions.assertEquals(2, db.size());
    }
}