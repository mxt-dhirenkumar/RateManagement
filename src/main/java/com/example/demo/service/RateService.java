package com.example.demo.service;

import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;
import com.example.demo.util.RateMergeUtil;
import com.example.demo.util.RateNormalizeUtil;
import com.example.demo.util.RateSplitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RateService {

    @Autowired
    private  RateRepository repository;

    @Transactional
    public Rate createRate(Rate rate) {
        // Normalize value and nights
        RateNormalizeUtil.normalize(rate);
        rate.setBookDateFrom(LocalDateTime.now());

        // Find overlapping active rates
        List<Rate> overlapping = repository.findByBungalowIdAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(
                rate.getBungalowId(), rate.getStayDateTo(), rate.getStayDateFrom());

        // Split overlapping rates
        if (!overlapping.isEmpty()) {
            RateSplitUtil.splitIfOverlapping(rate, overlapping, repository);
        }

        // Save new rate
        repository.save(rate);

        // Merge neighbors if possible
        return RateMergeUtil.mergeNeighbors(rate, repository);
    }
}
