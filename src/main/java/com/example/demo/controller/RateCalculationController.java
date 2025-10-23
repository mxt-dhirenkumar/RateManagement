package com.example.demo.controller;

import com.example.demo.dto.RateCalculationRequest;
import com.example.demo.dto.RateCalculationResponse;
import com.example.demo.service.RateCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
public class RateCalculationController {

    private final RateCalculationService rateCalculationService;

    public RateCalculationController(RateCalculationService rateCalculationService) {
        this.rateCalculationService = rateCalculationService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<RateCalculationResponse> calculateBill(@RequestBody RateCalculationRequest request) {
        RateCalculationResponse response = rateCalculationService.calculatePrice(request);
        return ResponseEntity.ok(response);
    }
}
