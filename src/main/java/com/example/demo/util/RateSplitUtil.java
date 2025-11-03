package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Utility class for splitting overlapping {@link Rate} entities.
 * <p>
 * The {@code splitIfOverlapping} method processes a new {@code Rate} and a list of existing overlapping rates.
 * For each overlapping rate:
 * <ul>
 *   <li>It closes the old rate by setting its booking end date to now and saving it.</li>
 *   <li>If the old rate starts before the new rate, it creates and saves a left segment for the non-overlapping period before the new rate.</li>
 *   <li>If the old rate ends after the new rate, it creates and saves a right segment for the non-overlapping period after the new rate.</li>
 * </ul>
 * This ensures that the date ranges of rates do not overlap, maintaining data consistency in the repository.
 */

public class RateSplitUtil {

    public static void splitIfOverlapping(Rate newRate, List<Rate> overlappingRates, RateRepository repository) {
        //LocalDateTime now = LocalDateTime.now();

        for (Rate oldRate : overlappingRates) {

            // Close old rate
            oldRate.setBookDateTo(newRate.getBookDateFrom());
            repository.save(oldRate);

            // Left piece (before newRate)
            if (oldRate.getStayDateFrom().isBefore(newRate.getStayDateFrom())) {
                Rate left = new Rate();
                left.setBungalowId(oldRate.getBungalowId());
                left.setStayDateFrom(oldRate.getStayDateFrom());
                left.setStayDateTo(newRate.getStayDateFrom().minusDays(1));
                left.setValue(oldRate.getValue());
                left.setNights(oldRate.getNights());
                left.setBookDateFrom(newRate.getBookDateFrom());
                repository.save(left);
            }

            // Right piece (after newRate)
            if (oldRate.getStayDateTo().isAfter(newRate.getStayDateTo())) {
                Rate right = new Rate();
                right.setBungalowId(oldRate.getBungalowId());
                right.setStayDateFrom(newRate.getStayDateTo().plusDays(1));
                right.setStayDateTo(oldRate.getStayDateTo());
                right.setValue(oldRate.getValue());
                right.setNights(oldRate.getNights());
                right.setBookDateFrom(newRate.getBookDateFrom());
                repository.save(right);
            }

            if(oldRate.getBookDateFrom().isAfter(oldRate.getBookDateTo())) {
                //  i delete this record as it is invalid now
                repository.delete(oldRate);
            }
        }
    }
}
