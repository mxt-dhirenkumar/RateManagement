package com.example.demo.util;

import com.example.demo.entity.Rate;

/**
 * Utility class for normalizing {@link Rate} entities.
 * <p>
 * The {@code normalize} method adjusts a {@code Rate} object so that if it represents
 * multiple nights (i.e., {@code nights} > 1), it converts the total value to a per-night value
 * by dividing the total value by the number of nights (rounded to the nearest whole number).
 * After normalization, the {@code nights} field is set to 1, ensuring the rate is always per night.
 * <p>
 * This helps standardize rate data for consistent processing and comparison.
 */

public class RateNormalizeUtil {

    public static void normalize(Rate rate) {
        if (rate.getNights() != null && rate.getNights() > 1) {
            long perNightValue = Math.round((double) rate.getValue() / rate.getNights());
            rate.setValue(perNightValue);
            rate.setNights(1);
        }
    }
}
