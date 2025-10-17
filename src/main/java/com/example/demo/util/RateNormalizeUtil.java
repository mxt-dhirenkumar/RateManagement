package com.example.demo.util;

import com.example.demo.entity.Rate;

public class RateNormalizeUtil {

    public static void normalize(Rate rate) {
        if (rate.getNights() != null && rate.getNights() > 1) {
            long perNightValue = Math.round((double) rate.getValue() / rate.getNights());
            rate.setValue(perNightValue);
            rate.setNights(1);
        }
    }
}
