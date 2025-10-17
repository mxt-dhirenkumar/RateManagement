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
    Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateToLessThanOrderByStayDateToDesc(
            Long bungalowId, LocalDate stayDateFrom);

    // 3️⃣ Find next active rate (the closest one after new stayDateTo)
    Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateFromGreaterThanOrderByStayDateFromAsc(
            Long bungalowId, LocalDate stayDateTo);

    // 4️⃣ Find all active rates overlapping a given date range
    List<Rate> findByBungalowIdAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(
            Long bungalowId, LocalDate stayDateTo, LocalDate stayDateFrom);
}
