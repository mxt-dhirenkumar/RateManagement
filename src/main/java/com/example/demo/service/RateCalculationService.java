package com.example.demo.service;

import com.example.demo.dto.RateCalculationRequest;
import com.example.demo.dto.RateCalculationResponse;
import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RateCalculationService {

    @Autowired
    private RateRepository rateRepository;


    public RateCalculationResponse calculatePrice(RateCalculationRequest request) {

        if (!request.getBookingDate().toLocalDate().isBefore(request.getStayDateFrom())) {
            throw new IllegalArgumentException("Booking date must be before stay start date");
        }

        // Convert bookingDate to string in seconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String bookingDateStr = request.getBookingDate().format(formatter);

        // Fetch relevant rates using truncated query
        List<Rate> rates = rateRepository.findRatesByBookingDateTruncated(request.getBungalowId(), bookingDateStr);

        if (rates.isEmpty()) {
            return new RateCalculationResponse(request.getBungalowId(), 0L, "No rates found for this booking date.");
        }

        long totalAmount = 0;

        for (Rate rate : rates) {
            // Calculate overlap between stay dates and rate stay dates
            // we find stayDateTo min and stayDateFrom max to find the overlapping days
            long days = Math.min(rate.getStayDateTo().toEpochDay(), request.getStayDateTo().toEpochDay())
                    - Math.max(rate.getStayDateFrom().toEpochDay(), request.getStayDateFrom().toEpochDay()) ;

            if (days > 0) {
                totalAmount += days * rate.getValue();
            }
        }

        return new RateCalculationResponse(request.getBungalowId(), totalAmount, "Price calculated successfully");
    }
}
