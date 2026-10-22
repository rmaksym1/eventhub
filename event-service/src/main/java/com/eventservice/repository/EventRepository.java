package com.eventservice.repository;

import com.eventservice.entity.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query("""
    select count(e) > 0 from Event e
    where e.venue.id = :venueId
      and e.id <> :excludeId
      and e.startsAt < :end
      and e.endsAt > :start
    """)
    boolean existsOverlappingExcluding(
            @Param("venueId") Long venueId,
            @Param("excludeId") Long excludeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    select count(e) > 0 from Event e
    where e.venue.id = :venueId
      and e.startsAt < :end
      and e.endsAt > :start
       """)
    boolean existsOverlapping(@Param("venueId") Long venueId,
                              @Param("start") LocalDateTime start,
                              @Param("end") LocalDateTime end);
}
