package com.example.demo.controller;

import com.example.demo.entity.Rate;
import com.example.demo.helper.RateExcelExporter;
import com.example.demo.helper.RateExcelHelper;
import com.example.demo.service.RateService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
@Validated
public class RateController {

    @Autowired
    private RateService service;

    @Autowired
    private RateExcelHelper excelHelper;

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


    @GetMapping("/export")
    public void exportRates(HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=rates.xlsx");

        List<Rate> rates = service.getAllRates();
        RateExcelExporter.export(response, rates);
    }

    @PostMapping("/import")
    public ResponseEntity<List<Rate>> importRates(@RequestParam("file") MultipartFile file) {
        if (!excelHelper.hasExcelFormat(file)) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            List<Rate> rates = excelHelper.importRatesFromExcel(file.getInputStream());
            return ResponseEntity.ok(rates);
        } catch (Exception e) {
            throw new RuntimeException("Failed to import Excel: " + e.getMessage());
        }
    }

}
