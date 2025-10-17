package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RateCalculationResponse {
    private Long bungalowId;
    private Long totalAmount;
    private String message;
}
