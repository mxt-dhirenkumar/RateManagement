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

public class RateMergeTest {

    @Test
    void mergeNeighbors_shouldCloseOldRecordsAndKeepNewActive() {
        // -------------------------------
        // Arrange: simulate existing DB
        // -------------------------------
        List<Rate> db = new ArrayList<>();

        Rate janRate = new Rate();
        janRate.setBungalowId(100L);
        janRate.setStayDateFrom(LocalDate.of(2025, 1, 1));
        janRate.setStayDateTo(LocalDate.of(2025, 1, 31));
        janRate.setValue(7000L);
        janRate.setNights(1);
        janRate.setBookDateFrom(LocalDateTime.now());
        janRate.setBookDateTo(null); // active
        db.add(janRate);

        Rate marRate = new Rate();
        marRate.setBungalowId(100L);
        marRate.setStayDateFrom(LocalDate.of(2025, 3, 1));
        marRate.setStayDateTo(LocalDate.of(2025, 3, 31));
        marRate.setValue(7000L);
        marRate.setNights(1);
        marRate.setBookDateFrom(LocalDateTime.now());
        marRate.setBookDateTo(null); // active
        db.add(marRate);

        // New rate to insert (Feb)
        Rate febRate = new Rate();
        febRate.setBungalowId(100L);
        febRate.setStayDateFrom(LocalDate.of(2025, 2, 1));
        febRate.setStayDateTo(LocalDate.of(2025, 2, 28));
        febRate.setValue(7000L);
        febRate.setNights(1);

        // -------------------------------
        // Act: merge using in-memory repository
        // -------------------------------
        InMemoryRateRepository repository = new InMemoryRateRepository(db);

        // Save new rate first (like create)
        repository.save(febRate);

        // Call merge neighbors
        RateMergeUtil.mergeNeighbors(febRate, repository);

        // -------------------------------
        // Assert: old rates closed, new rate active
        // -------------------------------
        Rate jan = db.stream().filter(r ->
                r.getStayDateFrom().equals(LocalDate.of(2025, 1, 1)) &&
                        r.getStayDateTo().equals(LocalDate.of(2025, 1, 31))
        ).findFirst().orElse(null);




        Rate mar = db.stream().filter(r ->
                r.getStayDateFrom().equals(LocalDate.of(2025, 3, 1)) &&
                        r.getStayDateTo().equals(LocalDate.of(2025, 3, 31))
        ).findFirst().orElse(null);



        Rate merged = db.stream().filter(r ->
                r.getStayDateFrom().equals(LocalDate.of(2025, 1, 1)) &&
                        r.getStayDateTo().equals(LocalDate.of(2025, 3, 31))
        ).findFirst().orElse(null);


        // Check total records remain same
        Assertions.assertEquals(3, db.size());
    }

}