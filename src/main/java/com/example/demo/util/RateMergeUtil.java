package com.example.demo.util;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public class RateMergeUtil {

    public static Rate mergeNeighbors(Rate newRate, RateRepository repository) {
        LocalDateTime now = LocalDateTime.now();

        Optional<Rate> prevOpt = repository.findTopByBungalowIdAndBookDateToIsNullAndStayDateToLessThanOrderByStayDateToDesc(
                newRate.getBungalowId(), newRate.getStayDateFrom());
        Optional<Rate> nextOpt = repository.findTopByBungalowIdAndBookDateToIsNullAndStayDateFromGreaterThanOrderByStayDateFromAsc(
                newRate.getBungalowId(), newRate.getStayDateTo());

        boolean merged = false;

        // Merge with previous
        if (prevOpt.isPresent()) {
            Rate prev = prevOpt.get();
            if (prev.getValue().equals(newRate.getValue()) && prev.getNights().equals(newRate.getNights())) {
                newRate.setStayDateFrom(prev.getStayDateFrom());
                prev.setBookDateTo(now);
                repository.save(prev);
                merged = true;
            }
        }

        // Merge with next
        if (nextOpt.isPresent()) {
            Rate next = nextOpt.get();
            if (next.getValue().equals(newRate.getValue()) && next.getNights().equals(newRate.getNights())) {
                newRate.setStayDateTo(next.getStayDateTo());
                next.setBookDateTo(now);
                repository.save(next);
                merged = true;
            }
        }

        // Save the new merged rate
        if (merged) {
            repository.save(newRate);
        }

        return newRate;
    }
}
