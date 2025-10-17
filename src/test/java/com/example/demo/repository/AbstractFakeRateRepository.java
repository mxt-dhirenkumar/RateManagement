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
        // if rate already exists, update; else add
        int index = db.indexOf(rate);
        if (index >= 0) {
            db.set(index, rate);
        } else {
            db.add(rate);
        }
        return rate;
    }

    // other methods throw UnsupportedOperationException
}

