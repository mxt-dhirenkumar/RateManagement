package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RateSplitUtil {

    public static void splitIfOverlapping(Rate newRate, List<Rate> overlappingRates, RateRepository repository) {
        LocalDateTime now = LocalDateTime.now();

        for (Rate oldRate : overlappingRates) {

            // Close old rate
            oldRate.setBookDateTo(now);
            repository.save(oldRate);

            // Left piece (before newRate)
            if (oldRate.getStayDateFrom().isBefore(newRate.getStayDateFrom())) {
                Rate left = new Rate();
                left.setBungalowId(oldRate.getBungalowId());
                left.setStayDateFrom(oldRate.getStayDateFrom());
                left.setStayDateTo(newRate.getStayDateFrom().minusDays(1));
                left.setValue(oldRate.getValue());
                left.setNights(oldRate.getNights());
                left.setBookDateFrom(now);
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
                right.setBookDateFrom(now);
                repository.save(right);
            }
        }
    }
}
