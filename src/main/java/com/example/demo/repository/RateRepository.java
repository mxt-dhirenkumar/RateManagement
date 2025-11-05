package com.example.demo.repository;

import com.example.demo.entity.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface RateRepository extends JpaRepository<Rate, Long> {



    /**
     * Finds the most recent active rate for a bungalow that ends exactly one day before a new rate starts,
     * with the same value and number of nights.
     *
     * @param bungalowId the ID of the bungalow
     * @param stayDateFromMinusOne the date one day before the new rate's start date
     * @param value the rate value
     * @param nights the number of nights
     * @return the previous active rate, if found
     */
    Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateToEqualsAndValueAndNightsOrderByStayDateToDesc(
            Long bungalowId,
            LocalDate stayDateFromMinusOne, // previous.stayDateTo = newRate.stayDateFrom - 1
            Long value,
            Integer nights
    );


    /**
     * Finds the next active rate for a bungalow that starts exactly one day after a new rate ends,
     * with the same value and number of nights.
     *
     * @param bungalowId the ID of the bungalow
     * @param stayDateToPlusOne the date one day after the new rate's end date
     * @param value the rate value
     * @param nights the number of nights
     * @return the next active rate, if found
     */
    Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateFromEqualsAndValueAndNightsOrderByStayDateFromAsc(
            Long bungalowId,
            LocalDate stayDateToPlusOne, // next.stayDateFrom = newRate.stayDateTo + 1
            Long value,
            Integer nights
    );


    /**
     * Finds all active rates for a bungalow that overlap with a given date range.
     * Only rates that are not booked (bookDateTo is null) are considered.
     *
     * @param bungalowId the ID of the bungalow
     * @param stayDateTo the end date of the range
     * @param stayDateFrom the start date of the range
     * @return list of overlapping active rates
     */
    List<Rate> findByBungalowIdAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(
            Long bungalowId, LocalDate stayDateTo, LocalDate stayDateFrom);


    /**
     * Finds all active rates for a bungalow with the same value and nights that overlap a given date range.
     * Only rates that are not booked (bookDateTo is null) are considered.
     *
     * @param bungalowId the ID of the bungalow
     * @param value the rate value
     * @param nights the number of nights
     * @param stayDateFrom the start date of the range
     * @param stayDateTo the end date of the range
     * @return list of matching overlapping active rates
     */
    List<Rate> findByBungalowIdAndValueAndNightsAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(
            Long bungalowId,
            Long value,
            Integer nights,
            LocalDate stayDateFrom,
            LocalDate stayDateTo
    );


    /**
     * Finds all rates for a bungalow that are active at a specific booking date and time (to the second).
     * Considers both open-ended and closed booking periods.
     *
     * @param bungalowId the ID of the bungalow
     * @param bookingDate the booking date and time as a string (format: yyyy-MM-dd HH:mm:ss)
     * @return list of rates active at the given booking date and time
     */

    @Query(value = """
    SELECT *
    FROM rates
    WHERE bungalow_id = :bungalowId
      AND (
            (DATE_FORMAT(book_date_from, '%Y-%m-%d %H:%i:%s') <= :bookingDate
             AND (book_date_to IS NOT NULL AND DATE_FORMAT(book_date_to, '%Y-%m-%d %H:%i:%s') >= :bookingDate))
            OR (DATE_FORMAT(book_date_from, '%Y-%m-%d %H:%i:%s') < :bookingDate AND book_date_to IS NULL)
          )
""", nativeQuery = true)
    List<Rate> findRatesByBookingDateTruncated(@Param("bungalowId") Long bungalowId,
                                               @Param("bookingDate") String bookingDate);



}
