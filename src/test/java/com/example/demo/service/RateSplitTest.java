package com.example.demo.service;

import com.example.demo.entity.Rate;
import com.example.demo.repository.InMemoryRateRepository;
import com.example.demo.util.RateSplitUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RateSplitTest {

    @Test
    void split_shouldCreateCorrectSegments_andCloseOriginal() {
        // -------------------------------
        // Arrange: simulate existing DB
        // -------------------------------
        List<Rate> db = new ArrayList<>();

        Rate existing = new Rate();
        existing.setBungalowId(100L);
        existing.setStayDateFrom(LocalDate.of(2025, 1, 1));
        existing.setStayDateTo(LocalDate.of(2025, 12, 31));
        existing.setValue(3000L);
        existing.setNights(1);
        existing.setBookDateFrom(LocalDateTime.now());
        existing.setBookDateTo(null); // active
        db.add(existing);

        // New rate to insert
        Rate newRate = new Rate();
        newRate.setBungalowId(100L);
        newRate.setStayDateFrom(LocalDate.of(2025, 3, 1));
        newRate.setStayDateTo(LocalDate.of(2025, 3, 31));
        newRate.setValue(4000L);
        newRate.setNights(1);

        // -------------------------------
        // Act: call split utility
        // -------------------------------

        // Create in-memory repository
        InMemoryRateRepository repository = new InMemoryRateRepository(db);

        // Find overlapping active rates
        List<Rate> overlapping = db.stream()
                .filter(r -> r.getBungalowId().equals(newRate.getBungalowId()) &&
                        r.getBookDateTo() == null &&
                        !r.getStayDateTo().isBefore(newRate.getStayDateFrom()) &&
                        !r.getStayDateFrom().isAfter(newRate.getStayDateTo()))
                .toList();

        // Call the split utility
        RateSplitUtil.splitIfOverlapping(newRate, overlapping, repository);

        // Save the new rate
        repository.save(newRate);

        // -------------------------------
        // Assert: DB should have 4 entries
        // -------------------------------
        Assertions.assertEquals(4, db.size());

        // 1st segment: Jan 1 → Feb 28 (split of original)
        Rate first = db.stream().filter(r ->
                r.getStayDateFrom().equals(LocalDate.of(2025, 1, 1)) &&
                        r.getStayDateTo().equals(LocalDate.of(2025, 2, 28)) &&
                        r.getBookDateTo() == null
        ).findFirst().orElse(null);
        Assertions.assertNotNull(first);
        Assertions.assertEquals(3000L, first.getValue());

        // 2nd segment: Apr 1 → Dec 31 (split of original)
        Rate third = db.stream().filter(r ->
                r.getStayDateFrom().equals(LocalDate.of(2025, 4, 1)) &&
                        r.getStayDateTo().equals(LocalDate.of(2025, 12, 31)) &&
                        r.getBookDateTo() == null
        ).findFirst().orElse(null);
        Assertions.assertNotNull(third);
        Assertions.assertEquals(3000L, third.getValue());

        // 3rd segment: new rate 01 Mar → 31 Mar
        Rate second = newRate;
        Assertions.assertEquals(LocalDate.of(2025, 3, 1), second.getStayDateFrom());
        Assertions.assertEquals(LocalDate.of(2025, 3, 31), second.getStayDateTo());
        Assertions.assertEquals(4000L, second.getValue());
        Assertions.assertNull(second.getBookDateTo());

        // 4th: original closed rate
        Rate closed = db.stream().filter(r ->
                r.getStayDateFrom().equals(LocalDate.of(2025, 1, 1)) &&
                        r.getStayDateTo().equals(LocalDate.of(2025, 12, 31)) &&
                        r.getBookDateTo() != null
        ).findFirst().orElse(null);
        Assertions.assertNotNull(closed);
    }
}
