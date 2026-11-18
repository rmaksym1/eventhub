package com.reservationservice.repository;

import com.reservationservice.entity.Reservation;
import com.reservationservice.entity.ReservationStatus;
import com.testcommon.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/sql/reservations-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ReservationRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("Should find reservation by user ID and reservation ID")
    void shouldFindByUserIdAndId() {
        Optional<Reservation> reservation = reservationRepository.findByUserIdAndId(3L, 1L);

        assertThat(reservation).isPresent();
        assertThat(reservation.get().getId()).isEqualTo(1L);
        assertThat(reservation.get().getUserId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("Should return empty when reservation exists but user ID does not match")
    void shouldNotFindByWrongUserId() {
        Optional<Reservation> reservation = reservationRepository.findByUserIdAndId(999L, 1L);

        assertThat(reservation).isEmpty();
    }

    @Test
    @DisplayName("Should return paginated reservations for a specific user")
    void shouldFindAllByUserIdPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> page = reservationRepository.findAllByUserId(pageable, 3L);

        assertThat(page).isNotNull();
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent())
                .extracting(Reservation::getUserId)
                .allMatch(userId -> userId.equals(3L));
    }

    @Test
    @DisplayName("Should find expired reservations by status and expiration timestamp")
    void shouldFindExpiredPendingReservations() {
        Instant now = Instant.parse("2026-01-01T00:00:00Z");

        List<Reservation> expiredReservations = reservationRepository.findAllByStatusAndExpiresAtBefore(
                ReservationStatus.PENDING,
                now
        );

        assertThat(expiredReservations)
                .hasSize(1)
                .extracting(Reservation::getId)
                .containsExactly(3L);
    }
}