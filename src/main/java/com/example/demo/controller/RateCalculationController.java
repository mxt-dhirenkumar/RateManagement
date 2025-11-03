package com.example.demo.controller;

import com.example.demo.dto.RateCalculationRequest;
import com.example.demo.dto.RateCalculationResponse;
import com.example.demo.service.RateCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
public class RateCalculationController {

    @Autowired
    private RateCalculationService rateCalculationService;

    /**
     * Calculates the bill for a given rate calculation request.
     *
     * @param request the rate calculation request containing necessary details
     * @return the calculated bill wrapped in a ResponseEntity
     */

    @PostMapping("/calculate")
    public ResponseEntity<RateCalculationResponse> calculateBill(@RequestBody RateCalculationRequest request) {
        RateCalculationResponse response = rateCalculationService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }
}
