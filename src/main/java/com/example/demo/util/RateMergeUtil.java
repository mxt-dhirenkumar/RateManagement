package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class RateMergeUtil {

    public static Rate mergeNeighbors(Rate newRate, RateRepository repository) {
        LocalDateTime now = LocalDateTime.now();

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
            prev.setBookDateTo(now);
            repository.save(prev);
            merged = true;
        }

        // Merge with next
        if (nextOpt.isPresent()) {
            Rate next = nextOpt.get();
            newRate.setStayDateTo(next.getStayDateTo());
            next.setBookDateTo(now);
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
