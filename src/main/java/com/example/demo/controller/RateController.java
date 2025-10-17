package com.example.demo.controller;

import com.example.demo.entity.Rate;
import com.example.demo.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rates")
@Validated
public class RateController {

    @Autowired
    private RateService service;

    @PostMapping
    public ResponseEntity<Rate> createRate(@Valid @RequestBody Rate rate) {
        Rate saved = service.createRate(rate);
        return ResponseEntity.ok(saved);
    }


}
