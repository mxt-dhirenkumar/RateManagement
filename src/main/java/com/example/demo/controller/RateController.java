package com.example.demo.controller;

import com.example.demo.entity.Rate;
import com.example.demo.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/rates")
@Validated
public class RateController {

    @Autowired
    private RateService service;

    /**
     * Creates a new Rate.
     *
     * @param rate the Rate object to be created (validated)
     * @return the created Rate wrapped in a ResponseEntity
     */

    @PostMapping
    public ResponseEntity<Rate> createRate(@Valid @RequestBody Rate rate) {
        Rate saved = service.createRate(rate);
        return ResponseEntity.ok(saved);
    }

    /**
     * Updates an existing Rate by its ID.
     *
     * @param id the ID of the Rate to update
     * @param rate the Rate object with updated data
     * @return the updated Rate wrapped in a ResponseEntity
     */

    @PutMapping("/{id}")
    public ResponseEntity<Rate> updateRate(@PathVariable("id") Long id, @RequestBody Rate rate) {
        Rate updated = service.updateRate(id, rate);
        return ResponseEntity.ok(updated);
    }


}
