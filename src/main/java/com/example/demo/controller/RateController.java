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

    @PutMapping("/{id}")
    public ResponseEntity<Rate> updateRate(@PathVariable("id") Long id, @RequestBody Rate rate) {
        Rate updated = service.updateRate(id, rate);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<Rate>> getAllRates() {
        return ResponseEntity.ok(service.getAllRates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rate> getRateById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getRateById(id));
    }

    @GetMapping("/bungalow/{bungalowId}")
    public ResponseEntity<List<Rate>> getRatesByBungalowId(@PathVariable Long bungalowId) {
        return ResponseEntity.ok(service.getRatesByBungalowId(bungalowId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRate(@PathVariable Long id) {
        service.deleteRate(id);
        return ResponseEntity.ok("Rate closed successfully.");
    }


}
