package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "rates")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;  // auto-incremented primary key

    @NotNull
    private Long bungalowId;  // which bungalow this rate belongs to

    @NotNull
    private LocalDate stayDateFrom;

    @NotNull
    private LocalDate stayDateTo;

    @Positive
    private Long value;  // always natural number, never points

    @Positive
    private Integer nights;  // always >0

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime bookDateFrom; // when client registered this rate

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime bookDateTo;  // null if active, set when closed

    //  Business rule: stayDateFrom <= stayDateTo
    @PrePersist
    @PreUpdate
    private void validateDates() {
        if (stayDateFrom.isAfter(stayDateTo)) {
            throw new IllegalArgumentException("stayDateFrom must be before or equal to stayDateTo");
        }
    }
}
