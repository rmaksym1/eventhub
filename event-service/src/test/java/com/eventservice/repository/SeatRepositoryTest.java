package com.eventservice.repository;

import com.eventservice.entity.QSeat;
import com.eventservice.entity.Seat;
import com.eventservice.entity.Section;
import com.testcommon.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/seat/seats-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class SeatRepositoryTest extends AbstractIntegrationTest {

    private static final QSeat SEAT = QSeat.seat;
    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Test
    @DisplayName("Should find seat by ID together with its section")
    void shouldFindSeatById() {
        Optional<Seat> found = seatRepository.findById(1L);

        assertThat(found).isPresent();
        assertThat(found.get().getRowNumber()).isEqualTo(1);
        assertThat(found.get().getSeatNumber()).isEqualTo(1);
        assertThat(found.get().getSection().getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should find seats by section ID using QueryDSL predicate")
    void shouldFindSeatsBySectionId() {
        Page<Seat> page = seatRepository.findAll(SEAT.section.id.eq(1L), PAGEABLE);

        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent())
                .extracting(Seat::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    @DisplayName("Should find seats by row and seat number across sections using QueryDSL predicate")
    void shouldFindSeatsByRowAndSeatNumber() {
        Page<Seat> page = seatRepository.findAll(
                SEAT.rowNumber.eq(1).and(SEAT.seatNumber.eq(1)),
                PAGEABLE
        );

        assertThat(page.getContent())
                .extracting(Seat::getId)
                .containsExactlyInAnyOrder(1L, 4L);
    }

    @Test
    @DisplayName("Should return empty page when no seat matches the predicate")
    void shouldReturnEmptyPageWhenNothingMatches() {
        Page<Seat> page = seatRepository.findAll(SEAT.rowNumber.eq(99), PAGEABLE);

        assertThat(page).isEmpty();
    }

    @Test
    @DisplayName("Should save a new seat with generated ID")
    void shouldSaveSeat() {
        Section section = sectionRepository.findById(2L).orElseThrow();

        Seat newSeat = new Seat();
        newSeat.setRowNumber(5);
        newSeat.setSeatNumber(7);
        newSeat.setSection(section);

        Seat saved = seatRepository.saveAndFlush(newSeat);

        assertThat(saved.getId()).isNotNull().isGreaterThan(4L);
    }

    @Test
    @DisplayName("Should throw exception when saving a duplicate seat (same section, row and number)")
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldNotSaveDuplicateSeat() {
        Section section = sectionRepository.findById(1L).orElseThrow();

        Seat duplicate = new Seat();
        duplicate.setRowNumber(1);
        duplicate.setSeatNumber(1);
        duplicate.setSection(section);

        assertThatThrownBy(() -> seatRepository.saveAndFlush(duplicate))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
