package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Utility for merging neighboring rate records for the same bungalow.
 * Handles both normal merges and reverse booking date cases.
 */
public class RateMergeUtil {

    public static Rate mergeNeighbors(Rate newRate, RateRepository repository) {
        // Find previous neighbor (just before newRate)
        Optional<Rate> prevOpt = repository.findTopByBungalowIdAndBookDateToIsNullAndStayDateToEqualsAndValueAndNightsOrderByStayDateToDesc(
                newRate.getBungalowId(),
                newRate.getStayDateFrom().minusDays(1),
                newRate.getValue(),
                newRate.getNights()
        );

        // Find next neighbor (just after newRate)
        Optional<Rate> nextOpt = repository.findTopByBungalowIdAndBookDateToIsNullAndStayDateFromEqualsAndValueAndNightsOrderByStayDateFromAsc(
                newRate.getBungalowId(),
                newRate.getStayDateTo().plusDays(1),
                newRate.getValue(),
                newRate.getNights()
        );

        boolean merged = false;

        // ---- Handle merge with previous ----
        if (prevOpt.isPresent()) {
            Rate prev = prevOpt.get();

            if (newRate.getBookDateFrom().isBefore(prev.getBookDateFrom())) {
                // special case: reverse booking date handling
                handleReverseBookingDateCase(newRate, prev, repository);
            } else {
                // normal merge
                newRate.setStayDateFrom(prev.getStayDateFrom());
                prev.setBookDateTo(newRate.getBookDateFrom());
                repository.save(prev);
            }
            merged = true;
        }

        // ---- Handle merge with next ----
        if (nextOpt.isPresent()) {
            Rate next = nextOpt.get();

            if (newRate.getBookDateFrom().isBefore(next.getBookDateFrom())) {
                // special case: reverse booking date handling
                handleReverseBookingDateCase(newRate, next, repository);
            } else {
                // normal merge
                newRate.setStayDateTo(next.getStayDateTo());
                next.setBookDateTo(newRate.getBookDateFrom());
                repository.save(next);
            }
            merged = true;
        }

        // Save the merged new rate if any merge happened
        if (merged) {
            repository.save(newRate);
        }

        return newRate;
    }

    /**
     * Handles cases where the new rate has an earlier booking date than its neighbor.
     * Instead of closing the newer record backwards, we:
     * - extend the neighbor’s stayDateFrom (or stayDateTo)
     * - close the new record properly
     */
    private static void handleReverseBookingDateCase(Rate newRate, Rate neighbor, RateRepository repository) {
        // Example case:
        // neighbor.bookDateFrom = 15 Feb, newRate.bookDateFrom = 15 Jan
        // neighbor.stayDateFrom = 1 Mar, newRate.stayDateTo = 28 Feb

        // 1️⃣ Extend neighbor’s stayDateFrom backward
        LocalDate newStart = newRate.getStayDateFrom().isBefore(neighbor.getStayDateFrom())
                ? newRate.getStayDateFrom()
                : neighbor.getStayDateFrom();

        neighbor.setStayDateFrom(newStart);

        // 2️⃣ Close new record with neighbor's bookDateFrom
        newRate.setBookDateTo(neighbor.getBookDateFrom());

        // 3️⃣ Save both records
        repository.save(neighbor);
        repository.save(newRate);
    }
}
