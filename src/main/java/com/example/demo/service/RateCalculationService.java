package com.example.demo.service;

import com.example.demo.dto.RateCalculationRequest;
import com.example.demo.dto.RateCalculationResponse;
import com.example.demo.entity.Rate;
import com.example.demo.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RateCalculationService {

    @Autowired
    private RateRepository rateRepository;

    /**
     * Calculates the total price for a booking based on the provided rate calculation request.
     * <p>
     * Validates that the booking date is before the stay start date. It then retrieves all applicable rates
     * for the bungalow at the given booking date, calculates the overlap in days between the requested stay
     * and each rate's period, and sums up the total amount. If no rates are found, returns a response with
     * a zero amount and an appropriate message.
     * </p>
     *
     * @param request the rate calculation request containing bungalow ID, booking date, and stay period
     * @return a response object with the bungalow ID, total calculated amount, and a status message
     * @throws IllegalArgumentException if the booking date is not before the stay start date
     */
    public RateCalculationResponse calculatePrice(RateCalculationRequest request) {
        if (!request.getBookingDate().toLocalDate().isBefore(request.getStayDateFrom())) {
            throw new IllegalArgumentException("Booking date must be before stay start date");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String bookingDateStr = request.getBookingDate().format(formatter);

        List<Rate> rates = rateRepository.findRatesByBookingDateTruncated(request.getBungalowId(), bookingDateStr);

        if (rates.isEmpty()) {
            return new RateCalculationResponse(request.getBungalowId(), 0L, "No rates found for this booking date.");
        }

        long totalAmount = calculateTotalAmount(
                rates,
                request.getStayDateFrom(),
                request.getStayDateTo()
        );

        return new RateCalculationResponse(request.getBungalowId(), totalAmount, "Price calculated successfully");
    }

    /**
     * Pure function to calculate the total amount for a stay given a list of rates and stay dates.
     *
     * @param rates      list of applicable rates
     * @param stayFrom   start date of the stay
     * @param stayTo     end date of the stay
     * @return total calculated amount
     */
    public static long calculateTotalAmount(List<Rate> rates, LocalDate stayFrom, LocalDate stayTo) {
        long totalAmount = 0;
        for (Rate rate : rates) {
            long days = Math.min(rate.getStayDateTo().toEpochDay(), stayTo.toEpochDay())
                    - Math.max(rate.getStayDateFrom().toEpochDay(), stayFrom.toEpochDay());
            if (days > 0) {
                totalAmount += days * rate.getValue();
            }
        }
        return totalAmount;
    }
}