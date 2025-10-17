package com.example.demo.repository;

import com.example.demo.entity.Rate;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryRateRepository extends AbstractFakeRateRepository {

    public InMemoryRateRepository(List<Rate> db) {
        super(db);
    }

    @Override
    public Rate save(Rate rate) {
        if (!db.contains(rate)) {
            db.add(rate);
        }
        return rate;
    }

    // Only implement methods used by RateSplitUtil
    public List<Rate> findByBungalowIdAndBookDateToIsNullOrderByStayDateFromAsc(Long bungalowId) {
        return db.stream()
                .filter(r -> r.getBungalowId().equals(bungalowId) && r.getBookDateTo() == null)
                .sorted((r1, r2) -> r1.getStayDateFrom().compareTo(r2.getStayDateFrom()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateToEqualsAndValueAndNightsOrderByStayDateToDesc(Long bungalowId, LocalDate stayDateFromMinusOne, Long value, Integer nights) {
        return Optional.empty();
    }

    @Override
    public Optional<Rate> findTopByBungalowIdAndBookDateToIsNullAndStayDateFromEqualsAndValueAndNightsOrderByStayDateFromAsc(Long bungalowId, LocalDate stayDateToPlusOne, Long value, Integer nights) {
        return Optional.empty();
    }

    @Override
    public List<Rate> findByBungalowIdAndBookDateToIsNullAndStayDateFromLessThanEqualAndStayDateToGreaterThanEqual(Long bungalowId, LocalDate stayDateTo, LocalDate stayDateFrom) {
        return List.of();
    }

    @Override
    public List<Rate> findByBungalowIdAndBookDateToIsNullAndValueAndNights(Long bungalowId, Long value, Integer nights) {
        return List.of();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Rate> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Rate> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Rate> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Rate getOne(Long aLong) {
        return null;
    }

    @Override
    public Rate getById(Long aLong) {
        return null;
    }

    @Override
    public Rate getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Rate> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Rate> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Rate> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Rate> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Rate> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Rate> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Rate, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Rate> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Rate> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Rate> findAll() {
        return List.of();
    }

    @Override
    public List<Rate> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Rate entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Rate> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Rate> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Rate> findAll(Pageable pageable) {
        return null;
    }
}
