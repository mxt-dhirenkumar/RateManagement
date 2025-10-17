package com.example.demo.controller;

import com.example.demo.dto.RateCalculationRequest;
import com.example.demo.dto.RateCalculationResponse;
import com.example.demo.service.RateCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rates")
@RequiredArgsConstructor
public class RateCalculationController {

    private final RateCalculationService rateCalculationService;

    @PostMapping("/calculate")
    public ResponseEntity<RateCalculationResponse> calculateRate(@RequestBody RateCalculationRequest request) {
        Long total = rateCalculationService.calculateTotalPrice(
                request.getBungalowId(),
                request.getStayDateFrom(),
                request.getStayDateTo()
        );

        RateCalculationResponse response = new RateCalculationResponse(
                request.getBungalowId(),
                total,
                "Bill calculated successfully."
        );

        return ResponseEntity.ok(response);
    }
}
