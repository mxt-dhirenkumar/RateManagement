package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Utility class for merging adjacent Rate entities in the database.
 * <p>
 * The main method, {@code mergeNeighbors}, attempts to merge a new Rate with its
 * immediate neighbors (previous and next) if they have contiguous date ranges,
 * the same value, and the same number of nights.
 * <ul>
 *   <li>If a previous neighbor exists (ending the day before the new Rate starts), it extends the new Rate's start date and marks the previous neighbor as merged.</li>
 *   <li>If a next neighbor exists (starting the day after the new Rate ends), it extends the new Rate's end date and marks the next neighbor as merged.</li>
 *   <li>After merging, the updated new Rate is saved to the repository.</li>
 * </ul>
 * This helps to keep the Rate records compact and avoid fragmentation of date ranges.
 */

public class RateMergeUtil {

    public static Rate mergeNeighbors(Rate newRate, RateRepository repository) {
        //LocalDateTime now = LocalDateTime.now();

        // Previous neighbor: stayDateTo = newRate.stayDateFrom - 1
        Optional<Rate> prevOpt = repository.findTopByBungalowIdAndBookDateToIsNullAndStayDateToEqualsAndValueAndNightsOrderByStayDateToDesc(
                newRate.getBungalowId(),
                newRate.getStayDateFrom().minusDays(1),
                newRate.getValue(),
                newRate.getNights()
        );

        // Next neighbor: stayDateFrom = newRate.stayDateTo + 1
        Optional<Rate> nextOpt = repository.findTopByBungalowIdAndBookDateToIsNullAndStayDateFromEqualsAndValueAndNightsOrderByStayDateFromAsc(
                newRate.getBungalowId(),
                newRate.getStayDateTo().plusDays(1),
                newRate.getValue(),
                newRate.getNights()
        );

        boolean merged = false;

        // Merge with previous
        if (prevOpt.isPresent()) {
            Rate prev = prevOpt.get();
            newRate.setStayDateFrom(prev.getStayDateFrom());
            prev.setBookDateTo(newRate.getBookDateFrom());
            repository.save(prev);
            merged = true;
        }

        // Merge with next
        if (nextOpt.isPresent()) {
            Rate next = nextOpt.get();
            newRate.setStayDateTo(next.getStayDateTo());
            next.setBookDateTo(newRate.getBookDateFrom());
            repository.save(next);
            merged = true;
        }

        // Save the new merged rate
        if (merged) {
            repository.save(newRate);
        }

        return newRate;
    }

}
