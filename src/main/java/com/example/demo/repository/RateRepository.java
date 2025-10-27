package com.example.demo.repository;

import com.example.demo.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RateRepository extends JpaRepository<Rate, Long> {

    // 1️⃣ Find all active rates for a bungalow (bookDateTo is null), ordered by stayDateFrom
    List<Rate> findByBungalowIdAndBookDateToIsNullOrderByStayDateFromAsc(Long bungalowId);

    // 2️⃣ Find previous active rate (the closest one before new stayDateFrom)
    Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateToEqualsAndValueAndNightsOrderByStayDateToDesc(
            Long bungalowId,
            LocalDate stayDateFromMinusOne, // previous.stayDateTo = newRate.stayDateFrom - 1
            Long value,
            Integer nights
    );


    // 3️⃣ Find next active rate (the closest one after new stayDateTo)
    Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateFromEqualsAndValueAndNightsOrderByStayDateFromAsc(
            Long bungalowId,
            LocalDate stayDateToPlusOne, // next.stayDateFrom = newRate.stayDateTo + 1
            Long value,
            Integer nights
    );


    // 4️⃣ Find all active rates overlapping a given date range
    List<Rate> findByBungalowIdAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(
            Long bungalowId, LocalDate stayDateTo, LocalDate stayDateFrom);

    // 5️⃣ Find all active rates with the same value and nights overlapping a given date range
    List<Rate> findByBungalowIdAndBookDateToIsNullAndValueAndNights(
            Long bungalowId, Long value, Integer nights);

    // 6 ️⃣ Find all rates for a bungalow
    List<Rate> findByBungalowId(Long bungalowId);

}
