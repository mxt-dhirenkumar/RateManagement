package com.example.demo.repository;

import com.example.demo.entity.Rate;

import java.util.List;

public abstract class AbstractFakeRateRepository implements RateRepository {
    protected final List<Rate> db;

    public AbstractFakeRateRepository(List<Rate> db) {
        this.db = db;
    }

    @Override
    public Rate save(Rate rate) {
        db.removeIf(r -> r.equals(rate)); // remove old if exists
        db.add(rate);                     // add updated
        return rate;
    }


    // other methods throw UnsupportedOperationException
}

