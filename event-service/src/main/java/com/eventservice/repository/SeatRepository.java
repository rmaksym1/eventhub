package com.eventservice.repository;

import com.eventservice.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface SeatRepository extends JpaRepository<Seat, Long>, QuerydslPredicateExecutor<Seat> {
}
