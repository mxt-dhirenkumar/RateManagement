package com.example.demo.service;

import com.example.demo.entity.Rate;
import com.example.demo.exception.DuplicateRateException;
import com.example.demo.repository.RateRepository;
import com.example.demo.util.RateValidationUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

class RateServiceDuplicateTest {

    @Test
    void shouldThrowExceptionWhenDuplicateRateExists() {
        // Given
        RateRepository mockRepository = Mockito.mock(RateRepository.class);

        // Existing rate: 1 Jan to 31 Dec
        Rate existingRate = new Rate();
        existingRate.setBungalowId(100L);
        existingRate.setStayDateFrom(LocalDate.of(2024, 1, 1));
        existingRate.setStayDateTo(LocalDate.of(2024, 12, 31));
        existingRate.setValue(1000L);
        existingRate.setNights(1);

        // Simulate that repository returns this rate as active
        Mockito.when(mockRepository.findByBungalowIdAndBookDateToIsNullAndValueAndNights(
                        100L, 1000L, 1))
                .thenReturn(List.of(existingRate));

        // New rate: 1 Mar to 31 Mar with same value and nights
        Rate newRate = new Rate();
        newRate.setBungalowId(100L);
        newRate.setStayDateFrom(LocalDate.of(2024, 3, 1));
        newRate.setStayDateTo(LocalDate.of(2024, 3, 31));
        newRate.setValue(1000L);
        newRate.setNights(1);

        // When / Then
        Assertions.assertThrows(DuplicateRateException.class, () -> {
            RateValidationUtil.checkDuplicate(newRate, mockRepository);
        });
    }


}
