package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RateCalculationRequest {
    private Long bungalowId;
    private LocalDate stayDateFrom;
    private LocalDate stayDateTo;
}
