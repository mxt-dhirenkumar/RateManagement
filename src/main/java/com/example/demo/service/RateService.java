package com.example.demo.service;

import com.example.demo.entity.Rate;
import com.example.demo.exception.DuplicateRateException;
import com.example.demo.exception.RateNotFoundException;
import com.example.demo.repository.RateRepository;
import com.example.demo.util.RateMergeUtil;
import com.example.demo.util.RateNormalizeUtil;
import com.example.demo.util.RateSplitUtil;
import com.example.demo.util.RateValidationUtil;
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

        // Check for duplicates
        RateValidationUtil.checkDuplicate(rate, repository);

        //(Optional : if user want to insert BookDateFrom that time you can remove this line)
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


    @Transactional
    public Rate updateRate(Long rateId, Rate updatedRate) {
        // Fetch existing rate
        Rate existing = repository.findById(rateId)
                .orElseThrow(() -> new RateNotFoundException("Rate not found with id: " + rateId));

        // Close the existing rate
        existing.setBookDateTo(LocalDateTime.now());
        repository.save(existing);

        // Call createRate for updated data
        return createRate(updatedRate);
    }

    public void deleteRate(Long id) {
        Rate rate = repository.findById(id)
                .orElseThrow(() -> new RateNotFoundException("Rate not found with id: " + id));

        rate.setBookDateTo(LocalDateTime.now());
        repository.save(rate);
    }

    public List<Rate> getAllRates() {
        return repository.findAll();
    }

    public Rate getRateById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RateNotFoundException("Rate not found with id: " + id));
    }

    public List<Rate> getRatesByBungalowId(Long bungalowId) {
        return repository.findByBungalowId(bungalowId);
    }
}
