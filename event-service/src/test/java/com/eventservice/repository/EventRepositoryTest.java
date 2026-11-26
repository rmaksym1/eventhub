package com.eventservice.repository;

import com.testcommon.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/event/events-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class EventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("Should return true when event overlaps with existing event in the same venue")
    void shouldReturnTrueWhenOverlappingExists() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 11, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 1, 13, 0);

        boolean exists = eventRepository.existsOverlapping(1L, start, end);

        assertThat(eventRepository.count()).isPositive();
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when event starts exactly when another event ends (adjacent intervals)")
    void shouldReturnFalseWhenIntervalsAreAdjacent() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 12, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 1, 14, 0);

        boolean exists = eventRepository.existsOverlapping(1L, start, end);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return false when overlapping time is in a different venue")
    void shouldReturnFalseWhenDifferentVenue() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 30);
        LocalDateTime end = LocalDateTime.of(2026, 6, 1, 11, 30);

        boolean exists = eventRepository.existsOverlapping(2L, start, end);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return false when overlapping event is the excluded event itself")
    void shouldReturnFalseWhenOverlappingEventIsExcluded() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 10, 30);
        LocalDateTime end = LocalDateTime.of(2026, 6, 1, 12, 30);

        boolean exists = eventRepository.existsOverlappingExcluding(1L, 1L, start, end);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return true when another non-excluded event overlaps in the same venue")
    void shouldReturnTrueWhenAnotherEventOverlapsDespiteExclusion() {
        LocalDateTime start = LocalDateTime.of(2026, 6, 1, 15, 0);
        LocalDateTime end = LocalDateTime.of(2026, 6, 1, 17, 0);

        boolean exists = eventRepository.existsOverlappingExcluding(1L, 1L, start, end);

        assertThat(eventRepository.count()).isPositive();
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return true when event exists and belongs to organizer")
    void shouldReturnTrueWhenEventBelongsToOrganizer() {
        Boolean exists = eventRepository.existsByIdAndOrganizerId(1L, 10L);

        assertThat(eventRepository.count()).isPositive();
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when event exists but organizer ID does not match")
    void shouldReturnFalseWhenOrganizerDoesNotMatch() {
        Boolean exists = eventRepository.existsByIdAndOrganizerId(1L, 999L);

        assertThat(exists).isFalse();
    }
}