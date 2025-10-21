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

public class MergeWithPreviousTest {

    @Test
    void mergeWithPrevious_shouldExtendPreviousRate() {
        // Arrange
        List<Rate> db = new ArrayList<>();

        // Existing March rate
        Rate march = new Rate();
        march.setBungalowId(100L);
        march.setStayDateFrom(LocalDate.of(2024, 3, 1));
        march.setStayDateTo(LocalDate.of(2024, 3, 31));
        march.setValue(3000L);
        march.setNights(1);
        march.setBookDateFrom(LocalDateTime.now());
        march.setBookDateTo(null);
        db.add(march);

        // New April rate to merge with previous
        Rate april = new Rate();
        april.setBungalowId(100L);
        april.setStayDateFrom(LocalDate.of(2024, 4, 1));
        april.setStayDateTo(LocalDate.of(2024, 4, 30));
        april.setValue(3000L);
        april.setNights(1);

        // Use your existing in-memory repository
        InMemoryRateRepository repo = new InMemoryRateRepository(db);

        // Act
        Rate result = RateMergeUtil.mergeNeighbors(april, repo);

        // Assert
        // The new merged record should extend from 1 Mar → 30 Apr
        Assertions.assertEquals(LocalDate.of(2024, 3, 1), result.getStayDateFrom());
        Assertions.assertEquals(LocalDate.of(2024, 4, 30), result.getStayDateTo());

        // The old March rate should now be closed
        Assertions.assertNotNull(march.getBookDateTo());

        // The merged (April) rate remains active
        Assertions.assertNull(result.getBookDateTo());

        // Database should now contain 2 entries: closed old + new merged
        Assertions.assertEquals(2, db.size());
    }
}
